(ns hphelperv2.model.minion
  "Tags objects with themes so we can filter things easily"
  (:require
    [taoensso.timbre :as log]

    #?@(:clj  [[hphelperv2.persist.persist :as p]
               ]
        :cljs [
               ]
             )
    )
  )

#?(:clj
  (do
    (defn create-minion!
      [minion]
      (p/insert! (-> minion (assoc :_class "Minion"))))
    (defn get-minions
      ([]
       (p/query (format "SELECT FROM Minion"))
       )
      )
    )
  )
