(ns ru.meldren.functionalprogramming.lab3.input
  (:require [clojure.string :as str]
            [ru.meldren.functionalprogramming.lab3.logger :refer [log log-new-line]]))

(defprotocol IInputRequester
  (request-input [this message parser])
  (request-string [this message])
  (request-number [this message parser range])
  (request-int [this message range])
  (request-long [this message range])
  (request-double [this message range]))

(defrecord InputRequester [logger]
  IInputRequester
  (request-input [this message parser]
    (log logger (str message ": "))
    (let [input (read-line)]
      (if (nil? input)
        (do
          (log-new-line logger "Завершение программы.")
          (System/exit 0))
        (let [trimmed-input (str/trim input)]
          (if (empty? trimmed-input)
            (request-input this message parser)
            (try
              (parser trimmed-input)
              (catch Exception _
                (log-new-line logger "Формат введенных вами данных не соответствует требуемому.")
                (request-input this message parser))))))))

  (request-string [this message]
    (request-input this message identity))

  (request-number [this message parser range]
    (if (and range (= (count range) 1))
      (first range)
      (let [create-range-parser
            (fn [parser range]
              (fn [input]
                (let [parsed-value (parser input)]
                  (when (and range (not (some #(= parsed-value %) range)))
                    (throw (IllegalArgumentException.)))
                  parsed-value)))]
        (request-input this message (create-range-parser parser range)))))

  (request-int [this message range]
    (request-number this message #(Integer/parseInt %) range))

  (request-long [this message range]
    (request-number this message #(Long/parseLong %) range))

  (request-double [this message range]
    (request-number this message #(Double/parseDouble (clojure.string/replace % #"," ".")) range)))
