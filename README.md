# FusionText - Powerful web-based code editing

FusionText is the intersection of a number of good ideas I've encountered over the years
that happens to implement an awesome programmer's editor and programming environment:

* Start with a small kernel that does nothing but bootstrap itself
* Modular, plugin-based design
* The program is written in (and using) itself
* Coding and program execution happen at the same time
* Use Lisp, because Lisp is the first and best language that can be evolved in itself
* Aggressively reuse existing best-of-breed components

## TODO

* Tech debt immediate cleanup
  * Make the js loader use the job api  *DONE*
  * Only load js that hasn't already been loaded (keep a set of already-loaded-things)
  * :dynamic maps need a :name to display in the job UI *DONE*

* Finish integrating replumb / implementing the main loader in repl.cljs

* Job API *DONE*
  * Job name
  * Step number / of? Or vector of step functions where each function accepts a continuation?
  * Root cells: Current step name/description; current step number; total steps in current job; jobs remaining
  * Run in the background (if possible?) as a progressive web app.  Else trigger the next step in a timer event.

* Mechanism to push jobs from back-end to front-end (as noted under Refactoring below)

* Edit itself using CodeMirror
  * Select a file
  * auto-repl displays result in gray beside line; hover shows more; guard against infinite loops

* Edit Scala using CodeMirror
  * Integrate with Ensime / SBT?
  * Use ScalaFiddle back-end?
  * Will need plugins to be able to extend the back-end

* Edit YAML / OpenAPI using the OpenAPI editor

* GFMX code notebooks

* JS editing support (use built-in CodeMirror code assist)

* Terminal emulation support https://cdnjs.com/libraries/xterm  (xtermjs)

* UI ideas
  * Splitters.  Ctrl-1, Ctrl-2, Ctrl-3, Ctrl-0 to add and remove.
  * Quick sidebar for things like quick outline, open-file tree, git status, etc.
  * File tree - type to filter
  * Need a way to compress the number of tabs to make manageable?
  * Synchronized editors of same file.  Across multiple tabs/connections.  Displaying remote highlight.
  * Synchronize the UI too?  (Open tabs / focused tab / more?)
    * UDP Multicast for windows / users finding each other?
    * https://github.com/ztellman/aleph
    * https://github.com/ztellman/aleph/issues/276
    * https://gist.github.com/gomes-work/e90b07192d9cfe6e9a31acd50b6d8b64
  * Libjitsi / audio & video?

* Refactoring
  * Move everything but bootstrap logic to separate repos as spiked in api.clj
  * Config.edn in each plugin repo's root naming the plugin, its activator, its dependencies
    * Dependencies allow us to compute load order
  * Link via the config mechanism
  * Write default config file if one doesn't already exist
  * Download webfusion-text via git during bootstrap
  * Get the UI up, then download and activate plugins in the background
    * Push activation jobs from the back-end to the UI via the refresh-state mechanism or similar *DONE*
    * `git pull` already-downloaded plugins before activating
    * Load everything we can, then display what we couldn't load (and dependencies) if needed
      * Just create an editor buffer in Markdown with the report?
    * Support Javascript-based plugins?

* Loader - See https://adzerk.com/blog/2017/02/faster-clojure-metadevelopment-with-boot/
  * Create standard ~/.fusion directory if needed and add standard config file
  * Allow to overload the standard ~/.fusion dirname on command line
  * Clone/pull bootstrap repo defined in config
  * (boot/load-file "${FUSION_DIR}/${BOOTSTRAP_PROJ}/build.boot")
  * (boot/boot web-dev)
  * Find a way to bridge this to SBT

## Reference

### Clojure
* EDN hack  https://stackoverflow.com/questions/15234880/how-to-use-clojure-edn-read-to-get-a-sequence-of-objects-in-a-file
* Foundation https://github.com/coconutpalm/clj-foundation/blob/master/src/clj_foundation/patterns.clj
* Specs https://clojure.org/guides/spec
* Orchestra https://github.com/jeaye/orchestra
* clojure.test https://clojure.github.io/clojure/clojure.test-api.html


### Clojurescript
* https://github.com/swannodette?page=3&tab=repositories
* https://github.com/arichiardi?tab=repositories


### Codemirror, general js libraries
* Change fn to λ, etc. https://github.com/creationix/tedit/blob/master/src-minimal/ui/code-mirror-editor.js
* hterm, ssh in js https://chromium.googlesource.com/apps/libapps/


### Git
#### Clojure
* https://github.com/clj-jgit/clj-jgit
* https://gist.github.com/arichiardi/370fda3d3f597e5379725a29a2ba8779

#### JS
* https://github.com/isomorphic-git/isomorphic-git
* https://isomorphic-git.org/docs/en/browser


### Boot
* https://adzerk.com/blog/2017/02/faster-clojure-metadevelopment-with-boot/



## License

Eclipse Public License, either version 1.0 or (at your option) any later version.

Copyright © 2017-2018 by David Orme
