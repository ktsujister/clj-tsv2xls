(defproject tsv2xls "0.0.7-SNAPSHOT"
  :description "create xls(or xlsx) file from tsv file."
  :dependencies [[org.clojure/clojure		"1.6.0"]
                 [org.clojure/tools.cli		"0.3.1"]
                 [org.apache.poi/poi		"3.11"]
                 [org.apache.poi/poi-ooxml	"3.11"]]
  :jvm-opts ["-Dfile.encoding=utf-8"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :main ^:skip-aot tsv2xls.core)
