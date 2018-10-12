(def task-options
  {:project 'coconutpalm/boot-code
   :version "0.1.1"
   :project-name "boot-code"
   :project-openness :open-source

   :description "A programmers' editor intended to be embedded inside of build tools."

   :scm-url "https://github.com/coconutpalm/boot-code"

   :test-sources "test"
   :test-resources nil})


(defn qualify [path] (str (System/getProperty "user.dir") "/" path))


(set-env!
 :dependencies '[[org.clojure/clojure       "1.9.0"]
                 [org.clojure/tools.cli     "0.4.1"]

                 [coconutpalm/boot-boot     "LATEST"]
                 [clojure-watch             "LATEST"]
                 [zcaudate/hara             "2.8.7"]
                 [adzerk/boot-cljs          "2.1.4"]
                 [adzerk/boot-reload        "0.6.0"]
                 [compojure                 "1.6.1"]
                 [hoplon/hoplon             "7.2.0"]
                 [hoplon/javelin            "3.9.0"]

                 [eval-soup                 "1.4.3"]
                 [paren-soup                "2.12.3"]

                 [org.clojure/clojurescript "1.10.238"]
                 [ring/ring-defaults        "0.3.2"]
                 [ring/ring-core            "1.7.0"]
                 [http-kit                  "2.3.0"]
                 [com.taoensso/sente        "1.13.1"]]


 :resource-paths #{(qualify "resources") (qualify "src/clj")}
 :source-paths   #{(qualify "src/cljs") (qualify "src/hl")})


(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-reload    :refer [reload]]
  '[hoplon.boot-hoplon    :refer [hoplon prerender]]
  '[pandeiro.boot-http    :refer [serve]])


(deftask cider "CIDER profile"
   []
   (require 'boot.repl)
   (swap! @(resolve 'boot.repl/*default-dependencies*)
          concat '[[org.clojure/tools.nrepl "0.2.13"]
                   [cider/cider-nrepl "0.18.0"]
                   [refactor-nrepl "2.4.0"]])
   (swap! @(resolve 'boot.repl/*default-middleware*)
          concat '[cider.nrepl/cider-middleware
                   refactor-nrepl.middleware/wrap-refactor])
   identity)


(deftask web-dev
  "Build boot-code for local development."
  []
  (comp
    (cider)
    (serve
     :port    7000
     :handler 'boot-code.handler/app
     ;; :init 'boot-code.strap/jetty-init
     :reload  true
     :httpkit true)
     (watch)
     (speak)
     (hoplon)
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
