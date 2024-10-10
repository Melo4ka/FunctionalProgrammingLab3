(ns ru.meldren.functionalprogramming.lab3.main
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [ru.meldren.functionalprogramming.lab3.logger :refer [log-new-line]]
            [ru.meldren.functionalprogramming.lab3.input :refer [request-double request-input request-int]]
            [ru.meldren.functionalprogramming.lab3.interpolation :refer [interpolate-by-lagrange interpolate-by-linear]])
  (:import (ru.meldren.functionalprogramming.lab3.input InputRequester)
           (ru.meldren.functionalprogramming.lab3.interpolation Point)
           (ru.meldren.functionalprogramming.lab3.logger ConsoleLogger)))

(def logger (ConsoleLogger.))

(def algorithms
  [{:name        "linear"
    :interpolate interpolate-by-linear}
   {:name        "lagrange"
    :interpolate interpolate-by-lagrange}])

(defn- subscript [n]
  (if (< n 10)
    (char (+ (int \u2080) n))
    (str/join (map subscript (map #(Character/digit ^char % 10) (str n))))))

(defn- parse-point [input]
  (let [numbers (str/split input #" ")]
    (if (= (count numbers) 2)
      (let [[x y] (map #(Double/parseDouble %) numbers)]
        (Point. x y))
      (throw (IllegalArgumentException.)))))

(defn- request-points [requester n]
  (doall
    (for [i (range n)]
      (let [subscript-number (subscript (inc i))]
        (request-input requester (str "Enter point (x" subscript-number " и y" subscript-number ")") parse-point)))))

(def cli-options
  [["-a" "--argument NUMBER" "Interpolation argument"
    :id :argument
    :parse-fn #(Integer/parseInt %)]
   ["-p" "--points NUMBER" "Points number"
    :id :points
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 2 % 12) "Must be a number between 2 and 12"]]
   ["-m" "--algorithm NAME" "Interpolation algorithm names"
    :id :algorithms
    :multi true
    :default []
    :update-fn conj
    :validate [#(some (fn [algorithm] (= (:name algorithm) %)) algorithms) "Must be an algorithm name"]]])

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (clojure.string/join \newline errors)))

(defn validate-args [args]
  (let [{:keys [options _ errors _]} (parse-opts args cli-options)]
    (cond
      errors
      {:exit-message (error-msg errors)}

      (or (empty? (:algorithm options)) (nil? (:points options)))
      {:exit-message "At least one algorithm name and number of points are required."}

      :else
      {:options options})))


(defn -main [& args]
  (let [{:keys [options exit-message]} (validate-args args)
        requester (InputRequester. logger)]
    (if exit-message
      (println exit-message)
      (while true
        (let [points (request-points requester (:points options))]
          (doseq [algorithm (:algorithms options)]
            (let [interpolate (:interpolate ((keyword algorithm) algorithms))]
              (log-new-line logger (str "Результат интерполяции алгоритмом " algorithm-name ": "
                                        (interpolate points x-value))))))))))
