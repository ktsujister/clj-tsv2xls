(defproject tsv2xls "0.0.3"
  :description "create xls(or xlsx) file from tsv file."
  :dependencies [[org.clojure/clojure		"1.3.0"]
                 [org.clojure/tools.cli		"0.2.1"]
                 [org.clojure/data.csv		"0.1.2"]
                 [org.apache.poi/poi		"3.8"]
                 [org.apache.poi/poi-ooxml	"3.8"]]
  :dev-dependencies [[swank-clojure		"1.4.0"]]
  :jvm-opts ["-Dfile.encoding=utf-8"]
  :main tsv2xls.core)
