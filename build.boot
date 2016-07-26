(set-env!
 :dependencies '[;; clojure
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/core.async "0.2.382"]

                 ;; java
                 [org.apache.kafka/kafka_2.11 "0.10.0.0"]
                 [org.apache.kafka/kafka-clients "0.10.0.0"]
                 ]
 )

(require '[clojure.tools.namespace.repl :refer [refresh]])
(import '[org.apache.kafka.clients.producer KafkaProducer ProducerRecord]
        '[org.apache.kafka.clients.consumer KafkaConsumer ConsumerRecord]
        '[java.util Properties])


(defn make-properties [properties-map]
  (let [props (Properties.)]
    (doseq [[k v] properties-map]
      (.put props k v))
    props))

;; ------- Producer -------
;; JavaDocs for creating a producer.
;; http://kafka.apache.org/0100/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html

(def default-producer-config {"bootstrap.servers" "localhost:9092,localhost:9093,localhost:9094"})

;; will want this to eventually define some defaults and look for a config.
(defn create-producer
  "Given a config map, create a Kafka producer."
  [config]
  (let [props-map (reduce (fn [acc [k v]]
                            (assoc acc (name k) v))
                          {"bootstrap.servers" "localhost:9092"
                           "acks"              "all"
                           "retries"           "0"
                           "batch.size"        "16384"
                           "linger.ms"         "1"
                           "buffer.memory"     "33554432"
                           "key.serializer"    "org.apache.kafka.common.serialization.StringSerializer"
                           "value.serializer"  "org.apache.kafka.common.serialization.StringSerializer"}
                          config)
        props     (make-properties props-map)]
    (KafkaProducer. props)))

(defn simple-producer [messages topic]
  (with-open [producer (create-producer default-producer-config)]
    (println producer)
    (dorun
     (map-indexed (fn [n message]
                    (let [message (ProducerRecord. (str topic) (str n) (str message))]
                      (println message)
                      (.send producer message)))
                  messages))))


;; ------- Consumer -------
;; JavaDocs for creating a consumer (group).
;; http://kafka.apache.org/0100/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html

(def default-consumer-config {"bootstrap.servers" "localhost:9092,localhost:9093,localhost:9094"})

(defn create-consumer
  "Given a config map, create a Kafka consumer."
  [config]
  (let [props-map (reduce (fn [acc [k v]]
                            (assoc acc (name k) v))
                          {"bootstrap.servers"       "localhost:9092"
                           "group.id"                "test"
                           "enable.auto.commit"      "true"
                           "auto.commit.interval.ms" "1000"
                           "session.timeout.ms"      "30000"
                           "buffer.memory"           "33554432"
                           "key.deserializer"        "org.apache.kafka.common.serialization.StringDeserializer"
                           "value.deserializer"      "org.apache.kafka.common.serialization.StringDeserializer"}
                          config)
        props     (make-properties props-map)]
    (KafkaConsumer. props)))

(defn simple-consumer [topic-list]
  (with-open [consumer (create-consumer default-consumer-config)]
    (do (.subscribe consumer topic-list))
    (loop []
      (let [records (.poll consumer 100)]
        (doseq [r records]
          (prn r)))
      (recur))))
