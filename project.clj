(defproject hphelperv2 "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[ch.qos.logback/logback-classic "1.2.3"]
                 [cider/cider-nrepl "0.17.0"]
                 [clj-time "0.14.4"]
                 [cljs-ajax "0.7.3"]
                 [compojure "1.6.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [cprop "0.1.11"]
                 [crypto-password "0.2.0"]
                 [differ "0.3.2"]
                 [eu.7bridges/clj-odbp "0.3.1-SNAPSHOT"]
                 [funcool/struct "1.2.0"]
                 [luminus-nrepl "0.1.4"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.0.2"]
                 [metosin/muuntaja "0.5.0"]
                 [metosin/ring-http-response "0.9.0"]
                 [mount "0.1.12"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238" :scope "provided"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.webjars.bower/tether "1.4.3"]
                 [org.webjars/bootstrap "4.0.0-2"]
                 [org.webjars/font-awesome "5.0.6"]
                 [re-frame "0.10.5"]
                 [reagent "0.7.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-servlet "1.6.3"]
                 [secretary "1.2.3"]
                 [selmer "1.11.7"]]

  :min-lein-version "2.0.0"
  
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot hphelperv2.core

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-kibit "0.1.2"]
            [lein-uberwar "0.2.0"]]
  :uberwar
  {:handler hphelperv2.handler/app
   :init hphelperv2.handler/init
   :destroy hphelperv2.handler/destroy
   :name "hphelperv2.war"}
  
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware
   [cemerick.piggieback/wrap-cljs-repl cider.nrepl/cider-middleware]}
  

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild
             {:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-dir "target/cljsbuild/public/js"
                 :output-to "target/cljsbuild/public/js/app.js"
                 :source-map "target/cljsbuild/public/js/app.js.map"
                 :optimizations :advanced
                 :pretty-print false
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}
                 :externs ["react/externs/react.js"]}}}}
             
             
             :aot :all
             :uberjar-name "hphelperv2.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-server" "-Dconf=dev-config.edn"]
                  :dependencies [[binaryage/devtools "0.9.9"]
                                 [com.cemerick/piggieback "0.2.2"]
                                 [day8.re-frame/re-frame-10x "0.3.0"]
                                 [directory-naming/naming-java "0.8"]
                                 [doo "0.1.10"]
                                 [figwheel-sidecar "0.5.15"]
                                 [luminus-http-kit "0.1.5"]
                                 [pjstadig/humane-test-output "0.8.3"]
                                 [prone "1.5.0"]
                                 [ring/ring-devel "1.6.3"]
                                 [ring/ring-mock "0.3.2"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]
                                 [lein-doo "0.1.10"]
                                 [lein-figwheel "0.5.15"]
                                 [org.clojure/clojurescript "1.10.238"]]
                  :cljsbuild
                  {:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :figwheel {:on-jsload "hphelperv2.core/mount-components"}
                     :compiler
                     {:main "hphelperv2.app"
                      :asset-path "/js/out"
                      :output-to "target/cljsbuild/public/js/app.js"
                      :output-dir "target/cljsbuild/public/js/out"
                      :source-map true
                      :optimizations :none
                      :pretty-print true
                      :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                      :preloads [day8.re-frame-10x.preload]}}}}
                  
                  
                  
                  :doo {:build "test"}
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-server" "-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]
                  :cljsbuild
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "hphelperv2.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true}}}}
                  
                  }
   :profiles/dev {}
   :profiles/test {}})
