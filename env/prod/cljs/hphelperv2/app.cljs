(ns hphelperv2.app
  (:require [hphelperv2.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
