(ns hphelperv2.auth.core
  "Deals with passwords, logging in, and api tokens"
  (:require [taoensso.timbre :as log]
            [hphelperv2.persist.persist :as db]
            [crypto.password.pbkdf2 :as password]
            ))

(def api-tokens
  "A map of api token to email address, and vice versa"
  (atom {:from-email {} :from-token {}}))

(defn- add-api-token
  [m email token]
  (-> m
      (assoc-in [:from-token token] email)
      (update-in [:from-email email] conj token)))
(defn add-api-token!
  [email token]
  (swap! api-tokens add-api-token email token))

(defn create-account
  [email password]
  (db/insert! {:_class "Login" :email email :password (password/encrypt password)}))

(defn login-user
  "If correct, returns a map with :email and :token. If failed, returns nil"
  [email password]
  (log/trace "login-user:" email password)
  (let [r (db/query (format "Select from Login where email = '%s'" email))]
    (log/trace "query response:" r)
    (if (= 1 (count r))
      ; We have a record
      (if (password/check password (:password (first r)))
        {::email email
         ::token (str (java.util.UUID/randomUUID))}
        nil)
      nil)))
