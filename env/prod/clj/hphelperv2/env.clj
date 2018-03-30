(ns hphelperv2.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[hphelperv2 started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[hphelperv2 has shut down successfully]=-"))
   :middleware identity})
