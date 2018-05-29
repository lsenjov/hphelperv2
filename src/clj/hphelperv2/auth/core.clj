(ns hphelperv2.auth.core
  "Deals with passwords, logging in, and api tokens"
  (:require [taoensso.timbre :as log]
            [hphelperv2.persist.persist :as db]
            [crypto.password.pbkdf2 :as password]
            ))

(def api-tokens
  "A map of api token to email address, and vice versa"
  (atom {:from-email {} :from-token {}}))

(defn- revoke-email
  [{:keys [from-email from-token] :as m} email]
  "Removes an email-token pair by the email"
  (let [token (from-email email)]
    (-> m
        (update-in [:from-token] dissoc token)
        (update-in [:from-email] dissoc email))))
(defn- revoke-token
  "Removes an email-token pair by the token"
  [{:keys [from-email from-token] :as m} token]
  (let [email (from-token token)]
    (-> m
        (update-in [:from-token] dissoc email)
        (update-in [:from-email] dissoc token))))
(defn- add-api-token
  [m email token]
  (log/trace "add-api-token" email)
  (-> m
      (assoc-in [:from-token token] email)
      (assoc-in [:from-email email] token)))
(defn add-api-token!
  [email token]
  (log/trace "add-api-token!" email)
  (swap! api-tokens add-api-token email token))

(defn create-account
  [email password]
  (db/insert! {:_class "Login" :email email :password (password/encrypt password)}))

(defn revoke-email!
  "Revokes an email from the list. Always returns nil"
  [email]
  (swap! api-tokens revoke-email email)
  nil)
(defn login-user!
  "If correct, returns a map with :email and :token. If failed, returns nil"
  [email password]
  (log/trace "login-user:" email password)
  (let [r (db/query (format "Select from Login where email = '%s'" email))]
    (log/trace "query response:" r)
    (if (pos? (count r))
      ; We have a record
      (if (password/check password (:password (first r)))
          (let [_ (log/trace "Password checked and fine")
                uuid (or
                       ;; If a token exists, return that, else return a new one
                       ;; Put it in the token list
                       (-> @api-tokens :from-email (get email))
                       (str (java.util.UUID/randomUUID)))]
            (log/trace "api token is:" uuid)
            (swap! api-tokens add-api-token email uuid)
            {::email email
             ::token uuid})
        nil)
      nil)))
