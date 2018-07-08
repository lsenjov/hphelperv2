(ns hphelperv2.test.persist.persist
  (:require [clojure.test :refer :all]
            [hphelperv2.persist.persist :as t]

            [clj-odbp.configure :as c]
            [clj-odbp.core :as odbp]
            [taoensso.timbre :as log]
            ))

;; Utilities, only to come in during testing
; If we load this up, we're using the test database
(intern 'hphelperv2.persist.persist 'db-name "test")
(defn reset-database!
  "Deletes ALL verticies from the database.
  Defined in a test namespace so this never enters a production database"
  []
  (log/info "Resetting database, deleting all")
  (t/command "DELETE FROM V UNSAFE")
  )
(defn purge-class!
  "Drops a class.
  Defined in a test namespace so this never enters prod"
  [c]
  (log/info "Dropping class: " c)
  (t/command (str "DROP CLASS " c " UNSAFE")))

(comment
  (reset-database!)
  (purge-class! "Tag")
  )
