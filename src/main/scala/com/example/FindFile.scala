package com.example

import com.sun.javafx.css.converters.PaintConverter.LinearGradientConverter

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ListView, TextField}
import scalafx.scene.layout.{BorderPane, HBox}

import java.io.File
import scala.annotation.tailrec
/**
  * Created by naoya on 2016/12/27.
  */
object FindFile extends JFXApp{
  // キーワード入力欄
  val keyword = new TextField {
    minWidth = 150
    promptText = "keyword"
  }

  // ディレクトリの入力欄
  val directory = new TextField {
    minWidth = 300
    promptText = "directory path"
  }

  // 結果一覧
  val matches = new ListView[File]

  // 画面の設定
  stage = new PrimaryStage {
    title = "Find File Sample"
    scene = new Scene {
      // 画面を上下に分割するためにBorderPaneを使う
      root = new BorderPane {
        // 上部には入力欄を配置
        top = new HBox {
          content = Seq(
            keyword,
            directory,
            new Button {
              text = "Search"
              // Searchボタンを押下時
              onAction = handle {
                matches.items = ObservableBuffer(
                  // 検索
                  find(keyword.text.value,
                    new File(directory.text.value))
                )
              }
            }
          )
        }
        // 下部に検索一覧を配置
        center = matches
      }
    }
  }

  // 指定したディレクトリ配下のファイルから，
  // ファイル名に指定したキーワードを含むものの一覧を取得．
  // @param keyword　キーワード
  // @param root 検索対象ディレクトリ
  // @return キーワードを含むファイル一覧

  def find(keyword: String, root: File): List[File] = {
    @tailrec
    def execute0(src: List[File], dest: List[File]): List[File] = {
      src match {
        case Nil => dest
        case head :: tail =>
          if (head.isDirectory)
          // ディレクトリの場合はサブディレクトリの一覧を
          // tailに追加して自分自身を呼び出す
            execute0(tail ++ head.listFiles.toList, dest)
          else if (head.getName.matches(s""".*$keyword.*"""))
          // キーワードに一致した場合は
          // ファイルをdestに追加して自分自身を呼び出す
            execute0(tail, head +: dest)
          else
          // キーワードに一致しない場合は何もせず自分自身を読み出す
            execute0(tail, dest)
      }
    }
    execute0(root.listFiles.toList, Nil)
  }
}
