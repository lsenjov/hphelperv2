(ns hphelperv2.test.auth.core
  (:require [clojure.test :refer :all]
            [hphelperv2.auth.core :as t]

            [hphelperv2.persist.persist :as p]
            [hphelperv2.test.persist.persist :as tp]
            ))

(use-fixtures
  :once
  (fn [f]
    (p/setup-database!)
    (tp/reset-database!)
    (f)))
(use-fixtures
  :each
  (fn [f]
    (tp/reset-database!)
    (f)
    ))

(deftest test-login
  (testing "Correct logins"
    ;; Called before actually creating the account
    (is (nil? (t/login-user "username" "password")))
    ;; Now create the account
    (t/create-account "username" "password")
    ;; Now it should be able to log in
    (is (t/login-user "username" "password"))
    )) ; TODO
