(ns ru.meldren.functionalprogramming.lab3.logger)

(def prefix "[FP] ")

(defprotocol ILogger
  (log [this message])
  (log-new-line [this message]))

(defrecord ConsoleLogger []
  ILogger
  (log [_ message]
    (print (str prefix message))
    (flush))
  (log-new-line [_ message]
    (println (str prefix message))
    (flush)))
