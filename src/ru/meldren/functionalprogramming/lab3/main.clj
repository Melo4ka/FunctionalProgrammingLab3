(ns ru.meldren.functionalprogramming.lab3.main
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [ru.meldren.functionalprogramming.lab3.input :refer [request-input]]
            [ru.meldren.functionalprogramming.lab3.interpolation :refer [interpolate-by-lagrange interpolate-by-linear]])
  (:import (ru.meldren.functionalprogramming.lab3.interpolation Point)))

(def algorithms {"linear"   interpolate-by-linear
                 "lagrange" interpolate-by-lagrange})

(def cli-options
  [["-g" "--algorithm NAME" "Interpolation algorithm names"
    :id :algorithms
    :multi true
    :default []
    :update-fn #(conj %1 (str/lower-case %2))
    :validate [#(contains? algorithms %) (str "Must be an algorithm name (" (str/join ", " (keys algorithms)) ")")]]
   ["-p" "--points NUMBER" "Points number"
    :id :points
    :parse-fn #(Integer/parseInt %)
    :validate [#(<= 2 % 12) "Must be a number between 2 and 12"]]
   ["-a" "--argument NUMBER" "Interpolation argument"
    :id :argument
    :parse-fn #(Integer/parseInt %)]
   ["-h" "--help"]])

(def points (ref []))

(defn- usage [options-summary]
  (->> ["Functional programming laboratory work #3:"
        ""
        "Usage: program-name options"
        ""
        "Options:"
        options-summary]
       (str/join \newline)))

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

(defn- request-point [p]
  (let [new-point (request-input "Enter point (x y)" parse-point)]
    (dosync
      (alter points conj new-point)
      (when (> (count @points) p)
        (alter points #(vec (rest %)))))))

(defn validate-args [args]
  (let [{:keys [options _ errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage summary)}

      errors
      {:exit-message (str "The following errors occurred while parsing your command:\n" (str/join \newline errors))}

      (or (empty? (:algorithms options)) (nil? (:points options)) (nil? (:argument options)))
      {:exit-message "At least one algorithm name, number of points and interpolation argument are required."}

      :else
      {:options options})))

(defn -main [& args]
  (let [{:keys [options exit-message]} (validate-args args)]
    (if exit-message
      (println exit-message)
      (while true
        (request-point (:points options))
        (when (>= (count @points) (:points options))
          (doseq [algorithm (:algorithms options)]
            (println (str (str/capitalize algorithm) " interpolation result: "
                          ((get algorithms algorithm) @points (:argument options))))))))))
