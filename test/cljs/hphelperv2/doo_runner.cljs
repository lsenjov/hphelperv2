(ns hphelperv2.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [hphelperv2.core-test]))

(doo-tests 'hphelperv2.core-test)

