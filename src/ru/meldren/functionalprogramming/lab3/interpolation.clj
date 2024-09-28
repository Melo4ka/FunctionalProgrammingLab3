(ns ru.meldren.functionalprogramming.lab3.interpolation)

(defrecord Point [x y])

(defn interpolate-by-linear [points x]
  (let [p0 (first points)
        p1 (second points)
        t (/ (- x (:x p0)) (- (:x p1) (:x p0)))]
    (+ (:y p0) (* t (- (:y p1) (:y p0))))))

(defn lagrange-coefficient [points i x]
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
