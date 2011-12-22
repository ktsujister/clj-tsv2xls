(defproject tsv2xls "0.0.1-SNAPSHOT"
  :description "create xls(or xlsx) file from tsv file."
  :dependencies [[org.clojure/clojure		"1.3.0"]
                 [org.clojure/tools.cli		"0.2.1"]
                 [org.clojure/data.csv		"0.1.0"]
                 [org.apache.poi/poi		"3.7"]
                 [org.apache.poi/poi-ooxml	"3.7"]]
  :dev-dependencies [[swank-clojure		"1.3.3"]]
  :jvm-opts ["-Dfile.encoding=utf-8"]
  :main tsv2xls.core)
