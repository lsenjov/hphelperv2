(ns hphelperv2.persist.persist
  (:require [clojure.core.async :as async :refer [<!!]]
            [clj-odbp.configure :as c]
            [clj-odbp.core :as odbp]
            [taoensso.timbre :as log]))

(def db-name "hphelper")
(defn connect-server [] (odbp/connect-server {:host "localhost" :port 2424} "root" "asdf"))
(defn connect-db [] (odbp/db-open {:host "localhost" :port 2424} db-name "root" "asdf"))

(defn- ensure-class-exists
  "Ensures a class exists in the database"
  [c]
  (try (with-open [session (connect-db)]
         (odbp/execute-command session (str "create class " c " extends V")))
       (catch Exception e (log/info "Class" c "already exists"))))

(defn query
  "Performs a query on the database"
  [q]
  (with-open [session (connect-db)]
    (odbp/query-command session q)))
(defn command
  "Performs a query on the database"
  [q]
  (with-open [session (connect-db)]
    (odbp/execute-command session q)))
(defn insert!
  "Inserts an object into the database"
  ([o]
   (with-open [session (connect-db)]
     (odbp/record-create session o)))
  ([o & more]
   (insert! o)
   (apply insert! more)))

(defn setup-database!
  "Creates the database and ensures all classes exist"
  []
  (log/info "Setting up database")
  (try (with-open [session (connect-server)]
         (odbp/db-create session db-name))
       (catch Exception e (log/info "Database" db-name "already exists")))
  ;; Properties for logins
  (ensure-class-exists "Login")
  (try (command "CREATE PROPERTY Login.email STRING")
       (catch Exception e nil))
  (try (command "CREATE INDEX Login.email UNIQUE")
       (catch Exception e nil))

  (ensure-class-exists "Tag")
  (try (command "CREATE PROPERTY Tag.tag_id STRING")
       (catch Exception e nil))
  (try (command "CREATE INDEX Tag.tag_id UNIQUE")
       (catch Exception e nil))
  )
(comment
  ;; This isn't needed very often, if at all
  (setup-database!)
  )
