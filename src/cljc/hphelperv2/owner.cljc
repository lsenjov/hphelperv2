(ns hphelperv2.owner
  "This model defines what things are able to be accessed on the current class.
  Owners are IDs, and ownership cascades.
  
  Information is considered public, unless locked down. When something is locked down, it is not checked further below to see if someone can see lower items"
  )

(comment
  "This is an example of what a data structure can look like"
  {::admin #{"admin-id"} ; The admins can see all information, all the time
   :info1 {:data1 1 :data2 2
           ::owners #{"owner"} ;; This is the people who can see all the information on this item
           ::public-keys #{:data2} ;; This is the list of keys that anyone can see.
           :data3 {:data3-1 31 :data3-2 32
                   ::owner #{"owner"}
                   ::public-keys #{:data3-2}
                   }
           }
   }
  )
