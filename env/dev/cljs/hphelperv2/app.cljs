(ns ^:figwheel-no-load hphelperv2.app
  (:require [hphelperv2.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
