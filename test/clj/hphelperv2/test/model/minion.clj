(ns hphelperv2.test.model.minion
  (:require [clojure.test :refer :all]
            [hphelperv2.model.minion :as t]
            [hphelperv2.persist.persist :as tpp]
            [hphelperv2.test.persist.persist :as ttpp]

            [clj-odbp.configure :as c]
            [clj-odbp.core :as odbp]
            [taoensso.timbre :as log]
            [clojure.spec.alpha :as s]
            ))

(use-fixtures
  :once
  (fn [f]
    (tpp/setup-database!)
    (f)))
(use-fixtures
  :each
  (fn [f]
    (ttpp/reset-database!)
    (f)))


(deftest basic-usage
  (testing "Basic Usage"
    (t/create-minion! {::name "TestMinion"})
    )
  )
