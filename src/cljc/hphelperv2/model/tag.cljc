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
    ))
(comment
  (find-all-tags)
  (find-all-tags "para")
  (format "SELECT * FROM Tag WHERE Tag.tag_id LIKE '%%%s%%'" "para")
  (create-tag! "paranoia")
  (create-tag! "classic")
  )
