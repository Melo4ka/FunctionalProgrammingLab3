(ns ru.meldren.functionalprogramming.lab3.main
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [ru.meldren.functionalprogramming.lab3.input :refer [request-input]]
            [ru.meldren.functionalprogramming.lab3.interpolation :refer [interpolate-by-lagrange interpolate-by-linear interpolate-range]])
  (:import (java.util Locale)
           (ru.meldren.functionalprogramming.lab3.interpolation Point)))

(def algorithms
  {"linear"   {:interpolate interpolate-by-linear :window-size 2}
   "lagrange" {:interpolate interpolate-by-lagrange :window-size 4}})

(def cli-options
  [["-a" "--algorithm NAME" "Interpolation algorithm name"
    :id :algorithms
    :multi true
    :default []
    :update-fn #(conj %1 (str/lower-case %2))
    :validate [#(contains? algorithms %) (str "Must be an algorithm name (" (str/join ", " (keys algorithms)) ")")]]
   ["-s" "--step NUMBER" "Step size for point calculation"
    :id :step
    :default 1.0
    :parse-fn #(Double/parseDouble %)]
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

(defn validate-args [args]
  (let [{:keys [options _ errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage summary)}

      errors
      {:exit-message (str "The following errors occurred while parsing your command:\n" (str/join \newline errors))}

      (empty? (:algorithms options))
      {:exit-message "At least one algorithm name is required."}

      :else
      {:options options})))

(defn- find-max-window-size [algorithm-names]
  (apply max (map #(get-in algorithms [% :window-size]) algorithm-names)))

(defn- parse-point [input]
  (let [numbers (str/split input #" ")]
    (if (= (count numbers) 2)
      (let [[x y] (map #(Double/parseDouble %) numbers)]
        (Point. x y))
      (throw (IllegalArgumentException.)))))

(defn- request-point [max-window-size]
  (dosync
    (alter points conj (request-input "Enter point (x y)" parse-point))
    (when (> (count @points) max-window-size)
      (alter points #(vec (rest %))))))

(defn- print-values [key result]
  (println (str/join "\t" (map #(String/format Locale/ENGLISH "%.2f" (into-array Object [(key %)])) result))))

(defn -main [& args]
  (let [{:keys [options exit-message]} (validate-args args)]
    (if exit-message
      (println exit-message)
      (let [max-window-size (find-max-window-size (:algorithms options))]
        (while true
          (request-point max-window-size)
          (doseq [algorithm-name (:algorithms options)]
            (let [window-size (get-in algorithms [algorithm-name :window-size])]
              (when (>= (count @points) window-size)
                (let [result (interpolate-range @points (:step options) window-size (get-in algorithms [algorithm-name :interpolate]))]
                  (println (str (str/capitalize algorithm-name) " interpolation result:"))
                  (print-values :x result)
                  (print-values :y result))))))))))
