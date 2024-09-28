(ns ru.meldren.functionalprogramming.lab3.main
  (:require [clojure.string :as str]
            [ru.meldren.functionalprogramming.lab3.logger :refer :all]
            [ru.meldren.functionalprogramming.lab3.input :refer :all]
            [ru.meldren.functionalprogramming.lab3.interpolation :refer :all])
  (:import (ru.meldren.functionalprogramming.lab3.input InputRequester)
           (ru.meldren.functionalprogramming.lab3.interpolation Point)
           (ru.meldren.functionalprogramming.lab3.logger ConsoleLogger)))

(def logger (ConsoleLogger.))

(def algorithms
  [{:name         "Линейная интерполяция"
    :points-range (range 2 3)
    :interpolate  interpolate-by-linear}
   {:name         "Метод Лагранжа"
    :points-range (range 2 13)
    :interpolate  interpolate-by-lagrange}])

(defn- subscript [n]
  (if (< n 10)
    (char (+ (int \u2080) n))
    (str/join (map subscript (map #(Character/digit ^char % 10) (str n))))))

(defn- request-algorithm [requester]
  (log-new-line logger "Доступные алгоритмы интерполяции:")
  (doseq [i (range (count algorithms))]
    (let [{:keys [name]} (nth algorithms i)]
      (log-new-line logger (str (inc i) ". " name))))
  (let [choice (request-int requester "Введите номер алгоритма" (range 1 (inc (count algorithms))))]
    (nth algorithms (dec choice))))

(defn- parse-point [input]
  (let [numbers (str/split input #" ")]
    (if (= (count numbers) 2)
      (let [[x y] (map #(Double/parseDouble %) numbers)]
        (Point. x y))
      (throw (IllegalArgumentException.)))))

(defn- request-points [requester points-range]
  (let [min-points (first points-range)
        max-points (last points-range)
        message (str "Введите количество точек (от " min-points " до " max-points ")")
        points-count (request-int requester message points-range)]
    (doall
      (for [i (range points-count)]
        (let [subscript-number (subscript (inc i))]
          (request-input requester (str "Введите точку (x" subscript-number " и y" subscript-number ")") parse-point))))))

(defn -main []
  (let [requester (InputRequester. logger)]
    (while true
      (let [{:keys [interpolate points-range]} (request-algorithm requester)
            points (request-points requester points-range)
            x-value (request-double requester "Введите x для интерполяции" nil)]
        (log-new-line logger (str "Результат интерполяции: " (interpolate points x-value)))))))
