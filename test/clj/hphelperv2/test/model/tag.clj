(ns hphelperv2.test.model.tag
  (:require [clojure.test :refer :all]
            [hphelperv2.model.tag :as t]
            [hphelperv2.persist.persist :as tpp]
            [hphelperv2.test.persist.persist :as ttpp]

            [clj-odbp.configure :as c]
            [clj-odbp.core :as odbp]
            [taoensso.timbre :as log]
            [clojure.spec.alpha :as s]
            ))

(s/def ::t/tag_id string?)

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
    (is (= 0 (count (t/find-all-tags))) "No tags should exist")
    (t/create-tag! "paranoia")
    (t/create-tag! "classic")
    (is (= 2 (count (t/find-all-tags))) "We created two tags")
    (is (= 1 (count (t/find-all-tags "para"))) "We should only find one tag")
    (is (= 1 (count (t/find-all-tags "ran"))) "We should only find one tag")
    (t/create-tag! "paranoia2")
    (is (= 2 (count (t/find-all-tags "para"))) "We should only find one tag")
    (is (t/find-single-tag "paranoia") "Should have returned a record")
    (is (not (t/find-single-tag "nonexistant")) "Should not have found anything")
    )
  )
