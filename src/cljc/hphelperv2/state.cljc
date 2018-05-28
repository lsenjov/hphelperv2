(ns hphelperv2.state
  "A library to manage stage changes"
  (:require [clojure.data :refer [diff]]
            [differ.core]
            [differ.diff]
            )
  )


(comment
  (def a1 {:a 1 :b 2 :c {:d 4 :e 5}})
  (def a2 {:a 2 :b 2 :c {:d 4 :e 6}})
  (diff a1 a2)
  (differ.core/patch a1 (differ.core/diff a1 a2))
  )
