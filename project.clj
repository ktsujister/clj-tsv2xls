(defproject tsv2xls "0.0.4"
  :description "create xls(or xlsx) file from tsv file."
  :dependencies [[org.clojure/clojure		"1.6.0"]
                 [org.clojure/tools.cli		"0.3.1"]
                 [org.clojure/data.csv		"0.1.2"]
                 [org.apache.poi/poi		"3.9"]
                 [org.apache.poi/poi-ooxml	"3.9"]]
  :jvm-opts ["-Dfile.encoding=utf-8"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :main ^:skip-aot tsv2xls.core)
