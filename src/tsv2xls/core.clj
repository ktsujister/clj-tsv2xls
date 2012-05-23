(ns tsv2xls.core
  (:use clojure.pprint
	clojure.stacktrace)
  (:require [clojure.tools.cli :as cli]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import [org.apache.poi.hssf.usermodel HSSFWorkbook]
           [org.apache.poi.xssf.streaming SXSSFWorkbook])
  (:gen-class))

(defn- get-output-file
  "get output filename from options."
  [{:keys [files outfile format] :as options}]
  {:pre [(seq format) (or files outfile)]}
  (if (seq outfile) outfile
      (clojure.string/replace (first files) #"(.+)\.txt" (str "$1." format))))

(def max-sheet-name-length 31)

(defn- get-sheet-name [file]
  "get sheetname from input file."
  (let [sheet-name-base (clojure.string/replace file #".*([^/]+)\.txt" "$1")]
    (apply str (take max-sheet-name-length sheet-name-base))))

(defn tsv-to-xls-convert
  "get tsv file as an input and create excel converted file."
  [{:keys [outfile format files encoding] :as options}]
  (let [outfile (get-output-file options)]
    (with-open [out-stream (io/output-stream outfile)]
      (let [book (case format
                   "xls" (HSSFWorkbook.)
                   "xlsx" (SXSSFWorkbook. 100))]
        (doseq [[i file] (map-indexed vector files)]
          (let [reader (if (seq encoding) (io/reader file :encoding encoding)
                           (io/reader file))
                records (csv/read-csv reader :separator \tab :quote \|)
                sheet (.createSheet book)
                sheet-name (get-sheet-name file)]
            (.setSheetName book i sheet-name)
            (doseq [[r row] (map-indexed vector records)]
              (let [hssf-row (.createRow sheet r)]
                (doseq [[c cell] (map-indexed vector row)]
                  (let [hssf-cell (.createCell hssf-row c)]
                    (.setCellValue hssf-cell cell)))))))
        (.write book out-stream)))))

(defn -main [& args]
  (let [[options rest banner]
        (cli/cli args
                 ["-o" "--outfile" "specify output file."]
                 ["-f" "--format" "specify output format.(xls|xlsx)" :default "xlsx"]
                 ["-e" "--encoding" "specify encoding."]
                 ["-?" "--help" "show help." :default false :flag true])]
    (let [{:keys [help]} options]
      (cond
       help (println banner)
       :else
       (try (tsv-to-xls-convert (assoc options :files rest))
            (catch Exception ex (print-stack-trace ex)))))))
