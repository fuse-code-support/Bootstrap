(def task-options
  {:project 'fusecode/bootstrap
   :version "0.1.1"
   :project-name "Bootstrap"
   :project-openness :open-source

   :description "Launch the fusecode web server and serve its minimal web SPA"

   :scm-url "https://github.com/fuse-code/Bootstrap.git"

   :test-sources "test"
   :test-resources nil})


(defn qualify [path] (str (System/getProperty "user.dir") "/" path))


(set-env!
 :dependencies '[[org.clojure/clojure       "1.9.0"]
                 [org.clojure/tools.cli     "0.4.1"]
                 [org.clojure/core.async    "0.4.474"]
                 [coconutpalm/boot-boot     "LATEST" :scope "test"] ; Standard Boot tasks

                 [coconutpalm/boot-server   "0.9.2"]
                 [clojure-watch             "LATEST"]
                 [adzerk/boot-cljs          "2.1.4"]
                 [adzerk/boot-reload        "0.6.0"]
                 [samestep/boot-refresh     "0.1.0"]

                 ;; web client/server framework
                 [compojure                 "1.6.1"]
                 [hoplon/hoplon             "7.2.0"]
                 [hoplon/javelin            "3.9.0"]

                 ;; logging
                 [com.taoensso/timbre       "4.10.0"]
                 [com.palletops/log-config  "0.1.4"]

                 ;; forward Java logging to Timbre
                 [org.slf4j/slf4j-api       "1.7.25"]
                 [com.fzakaria/slf4j-timbre "0.3.12"]
                 [org.slf4j/log4j-over-slf4j "1.7.14"]
                 [org.slf4j/jul-to-slf4j    "1.7.14"]
                 [org.slf4j/jcl-over-slf4j  "1.7.14"]

                 ;; graphql
                 [com.walmartlabs/lacinia   "0.31.0-rc-1"]
                 [locksmith                 "0.1.0"]

                 ;; basic auto-eval browser-based editor
                 [eval-soup                 "1.4.3"]
                 [paren-soup                "2.12.3"] ; Use to help bootstrap CodeMirror?

                 [org.clojure/clojurescript "1.10.238"]
                 [ring/ring-defaults        "0.3.2"]
                 [ring/ring-core            "1.7.0"]
                 [org.immutant/web          "2.1.10" :exclusions [ch.qos.logback/logback-classic]]
                 [com.taoensso/sente        "1.13.1"]] ; websocket support


 :resource-paths #{(qualify "resources") (qualify "src/clj") (qualify "src/cljc")}
 :source-paths   #{(qualify "src/cljs") (qualify "src/hl")})


(require
 '[clj-boot.core         :refer :all]
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-reload    :refer [reload]]
 '[samestep.boot-refresh :refer [refresh]]
 '[hoplon.boot-hoplon    :refer [hoplon prerender]]
 '[coconutpalm.boot-http :refer [serve]])


(deftask web-dev
  "Build boot-code for local development."
  []
  (comp
   (cider)
   (serve
    :port     7000
    :immutant true
    :ssl      true
    :handler 'fusecode.server.random/app
    :init 'fusecode.strap/jetty-init)
   (watch)
   (speak)
   (hoplon)
   (refresh)
   (reload)
   (repl :server true)
   (cljs)))


;; WIP!!!
#_(deftask isolated
    "Run task requiring dependencies in its own pod"
    [dependencies task & args]
    (fn []
      (pod/make-pod
       (update-in (b/get-env)
                  [:dependencies]
                  (identity [(dep "org.clojure/clojure" "1.9.0")
                             (dep "boot/core" (:boot-clj-version @config/settings))
                             (dep "boot/pod" (:boot-clj-version @config/settings))
                             (dep "boot/worker" (:boot-clj-version @config/settings))])))))


;; This is specialized for launching via fusion-boot only
;;
;; We have to do this explicitly because 'boot --file something.boot a-task' does not
;; launch a-task but instead loads something.boot, then attempts to call -main with
;; "a-task" as an argument.
(defn -main [& args]
  (letfn [(poll-reload []
            (let [f (java.io.File. "./build.boot")]
              (loop [mtime (.lastModified f)]
                (let [new-mtime (.lastModified f)]
                  (when (> new-mtime mtime)
                    (load-file (.getCanonicalPath f)))
                  (Thread/sleep 1000)
                  (recur new-mtime)))))

          (run-task [task-name]
            (require '[clojure.tools.reader :refer [read-string]])

            (let [launch-command (str "(boot (" task-name "))")
                  form           (read-string launch-command)
                  reloader       (Thread. poll-reload)]

              (.setDaemon reloader true)
              (.start reloader)

              (eval form)))]

    (cond
      (> (count args) 1) (println "Usage: build.boot <task-name>")
      (= (count args) 1) (run-task (first args))
      :else              (println "No task specified.  Usage: build.boot <task-name>"))))
