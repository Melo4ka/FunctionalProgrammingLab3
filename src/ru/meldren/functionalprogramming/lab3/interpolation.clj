(ns ru.meldren.functionalprogramming.lab3.interpolation)

(defrecord Point [x y])

(defn interpolate-by-linear [points x]
  (let [[p0 p1] (take-last 2 points)
        t (/ (- x (:x p0)) (- (:x p1) (:x p0)))]
    (+ (:y p0) (* t (- (:y p1) (:y p0))))))

(defn- lagrange-coefficient [points i x]
  (reduce
    (fn [acc j]
      (if (= i j)
        acc
        (* acc
           (- x (:x (nth points j)))
           (/ 1 (- (:x (nth points i)) (:x (nth points j)))))))
    1
    (range (count points))))

(defn interpolate-by-lagrange [points x]
  (reduce
    (fn [result i]
      (+ result (* (:y (nth points i)) (lagrange-coefficient points i x))))
    0
    (range (count points))))

(defn- generate-steps [x-min x-max step]
  (let [sequence (take-while #(<= % x-max) (iterate #(+ % step) x-min))
        last-value (+ (last sequence) step)]
    (if (= (last sequence) x-max)
      (vec sequence)
      (conj (vec sequence) last-value))))

(defn interpolate-range [points step window-size interpolate]
  (let [window (take-last window-size points)
        x-min (:x (first window))
        x-max (:x (last window))]
    (mapv (fn [x] (->Point x (interpolate window x))) (generate-steps x-min x-max step))))
