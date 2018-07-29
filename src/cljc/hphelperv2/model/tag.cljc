(ns hphelperv2.model.tag
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
    (defn find-all-tags
      ([]
       (map :tag_id (p/query "SELECT * FROM Tag")))
      ([s]
       (map :tag_id (p/query (format "SELECT * FROM Tag WHERE tag_id LIKE '%%%s%%'" s)))))
    (defn find-single-tag
      ([s]
       (-> (p/query (format "SELECT * FROM Tag WHERE tag_id LIKE '%s'" s)) first)))
    (defn create-tag!
      [n]
      (p/insert! {:tag_id n :_class "Tag"}))
    (defn add-tag-to-item
      [i t]
      nil)
    ))
(comment
  (find-all-tags)
  (find-all-tags "para")
  (format "SELECT * FROM Tag WHERE Tag.tag_id LIKE '%%%s%%'" "para")
  (create-tag! "paranoia")
  (create-tag! "classic")

  (defn map->nsmap
    [m n]
    (reduce-kv (fn [acc k v]
                 (let [new-kw (if (and (keyword? k)
                                       (not (#{:_class :_rid} k))
                                       (not (qualified-keyword? k)))
                                (keyword (str n) (name k))
                                k) ]
                   (assoc acc new-kw v)))
               {} m))
  (def testa (find-single-tag "paranoia"))
  (p/upsert! (-> testa (map->nsmap *ns*)))
  (p/query "SELECT FROM Tag")
  )
