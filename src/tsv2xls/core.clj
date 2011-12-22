(ns tsv2xls.core
  (:use clojure.pprint)
  (:require [clojure.tools.cli :as cli]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import [org.apache.poi.hssf.usermodel HSSFWorkbook]
           [org.apache.poi.xssf.usermodel XSSFWorkbook])
  (:gen-class))

(defn tsv-to-xls-convert
  "get tsv file as an input and create excel converted file."
  [{:keys [infile outfile mode name]}]
  (with-open [in-file (io/reader infile)
              out-stream (io/output-stream outfile)]
    (let [records (csv/read-csv in-file :separator \tab)
          book (cond (= mode "xls") (HSSFWorkbook.)
                     (= mode "xlsx") (XSSFWorkbook.))
          sheet (.createSheet book)]
      (.setSheetName book 0 name)
      (doseq [[i row] (map vector (iterate inc 0) records)]
        (let [hssf-row (.createRow sheet i)]
          (doseq [[j cell] (map vector (iterate inc 0) row)]
            (let [hssf-cell (.createCell hssf-row j)]
              (.setCellValue hssf-cell cell)))))
      (.write book out-stream))))

(defn -main [& args]
  (let [[options rest banner]
        (cli/cli args
                 ["-i" "--infile" "specify input file(TSV)."]
                 ["-o" "--outfile" "specify output file."]
                 ["-m" "--mode" "specify mode.(xls|xlsx)" :default "xlsx"]
                 ["-n" "--name" "specify sheetname." :default "sheet"]
                 ["-?" "--help" "show help." :default false :flag true])]
    (let [{:keys [mode name infile outfile help]} options]
      (cond
       help (println banner)
       :else (tsv-to-xls-convert options)))))
