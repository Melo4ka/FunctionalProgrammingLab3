# Лабораторная работа №3: Функциональное программирование

**Студент**: Андриенко Сергей Вячеславович

**Группа**: P34092

## Цель работы

Получить навыки работы с вводом/выводом, потоковой обработкой данных, командной строкой.

## Требования

Реализовать лабораторную работу по предмету "Вычислительная математика" посвященную интерполяции со следующими
дополнениями:

1. Реализовать методы интерполяции:
    * Линейная интерполяция
    * Метод Лагранжа
2. Настройки программы должны задаваться через аргументы командной строки:
    * Используемые алгоритмы
    * Размер шага
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
```

## Пример одного цикла работы программы

```
meldren@Mac-Sergej FunctionalProgrammingLab3 % clj -M -m ru.meldren.functionalprogramming.lab3.main -a linear -a lagrange 
Enter point (x y): 
0 0
Enter point (x y): 
1.571 1
Linear interpolation result:
0.00	1.00	2.00
0.00	0.64	1.27
Enter point (x y): 
3.142 0
Linear interpolation result:
1.57	2.57	3.57
1.00	0.36	-0.27
Enter point (x y): 
4.712 -1
Linear interpolation result:
3.14	4.14	5.14
0.00	-0.64	-1.27
Lagrange interpolation result:
0.00	1.00	2.00	3.00	4.00	5.00
0.00	0.97	0.84	0.12	-0.67	-1.03
Enter point (x y): 
12.568 0
Linear interpolation result:
4.71	5.71	6.71	7.71	8.71	9.71	10.71	11.71	12.71
-1.00	-0.87	-0.75	-0.62	-0.49	-0.36	-0.24	-0.11	0.02
Lagrange interpolation result:
1.57	2.57	3.57	4.57	5.57	6.57	7.57	8.57	9.57	10.57	11.57	12.57
1.00	0.37	-0.28	-0.91	-1.49	-1.95	-2.26	-2.38	-2.25	-1.84	-1.11	0.00
```

## Заключение

В ходе выполнения лабораторной работы были изучены функции Clojure для работы с аргументами командной строки, вводом и
выводом, а также
организована обработка данных в потоковом режиме. Благодаря использованию функционального языка реализация методов
интерполяции оказалась очень простой и понятной.