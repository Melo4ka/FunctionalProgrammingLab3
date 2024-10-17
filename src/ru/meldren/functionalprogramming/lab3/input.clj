(ns ru.meldren.functionalprogramming.lab3.input
  (:require [clojure.string :as str]))

(defn request-input [message parser]
  (println (str message ":"))
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

(defn request-string [message]
  (request-input message identity))

(defn request-number [message parser range]
  (if (and range (= (count range) 1))
    (first range)
    (let [create-range-parser
          (fn [parser range]
            (fn [input]
              (let [parsed-value (parser input)]
                (when (and range (not (some #(= parsed-value %) range)))
                  (throw (IllegalArgumentException.)))
                parsed-value)))]
      (request-input message (create-range-parser parser range)))))

(defn request-int [message range]
  (request-number message #(Integer/parseInt %) range))

(defn request-long [message range]
  (request-number message #(Long/parseLong %) range))

(defn request-double [message range]
  (request-number message #(Double/parseDouble (str/replace % #"," ".")) range))
