# Лабораторная работа №3: Функциональное программирование

**Студент**: Андриенко Сергей Вячеславович

**Группа**: P34092

## Цель работы

Получить навыки работы с вводом/выводом, потоковой обработкой данных, командной строкой.

## Требования

Реализовать лабораторную работу по предмету "Вычислительная математика" посвящённую аппроксимации со следующими
дополнениями:

1. Реализовать методы интерполяции:
    * Линейная интерполяция
    * Метод Лагранжа
2. Настройки программы должны задаваться через аргументы командной строки:
    * Используемые алгоритмы
    * Количество точек (от 2 до 12)
    * Аргумент для интерполяции (x)
3. Входные данные должны подаваться на стандартный ввод:
    * Значения каждой точки в формате "x<sub>i</sub> y<sub>i</sub>"
4. Выходные данные должны подаваться на стандартный вывод:
    * Найденные каждым заданным алгоритмом значения функции f(x).
5. Программа должна работать в потоковом режиме.

## Описание реализации

Функция для запроса ввода от пользователя через стандартный ввод с преобразованием введенных данных в нужный тип:

```clojure
(defn request-input [message parser]
  (println (str message ": "))
  (let [input (read-line)]
    (if (nil? input)
      (System/exit 0)
      (let [trimmed-input (str/trim input)]
        (if (empty? trimmed-input)
          (request-input message parser)
          (try
            (parser trimmed-input)
            (catch Exception _
              (println "The format of your input is invalid")
              (request-input message parser))))))))
```

Функция для реализации линейной интерполяции:

```clojure
(defn interpolate-by-linear [points x]
  (let [p0 (first points)
        p1 (second points)
        t (/ (- x (:x p0)) (- (:x p1) (:x p0)))]
    (+ (:y p0) (* t (- (:y p1) (:y p0))))))
```

Функция для реализации интерполяции методом Лагранжа:

```clojure
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
```

## Пример одного цикла работы программы

```
meldren@Mac-Sergej FunctionalProgrammingLab3 % clj -M -m ru.meldren.functionalprogramming.lab3.main -g lagrange -g linear -p 3 -a 5
Enter point (x₁ y₁): 
1 2
Enter point (x₂ y₂): 
100 101
Enter point (x₃ y₃): 
-14 -13
Lagrange interpolation result: 6.0
Linear interpolation result: 6.0
```

## Заключение

В ходе выполнения лабораторной работы были изучены функции Clojure для работы с аргументами командной строки, вводом и
выводом, а также
организована обработка данных в потоковом режиме. Благодаря использованию функционального языка реализация методов
интерполяции оказалась очень простой и понятной.