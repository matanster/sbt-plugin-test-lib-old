package org.canve.sbtPluginTestLib

import java.io.{PrintWriter, BufferedWriter, OutputStreamWriter, FileOutputStream, Closeable, Flushable}
import org.fusesource.jansi.AnsiOutputStream
import scala.sys.process._
import java.io.File

/*
 * Takes care of routing a process's stdout and stderr to a file, being a proper 
 * ProcessorLogger callback object for Scala's ProcessBuilder methods. Inspired by 
 * the original FileProcessorLogger in scala.sys.process.
 */
class FilteringOutputWriter(outFile: File) extends ProcessLogger {
  
  private val ansiFilteringStream = new AnsiOutputStream(new FileOutputStream(outFile, true))
  
  private val writer = (
    new PrintWriter(
      new BufferedWriter(
        new OutputStreamWriter(
          ansiFilteringStream
        )
      )
    )
  )  
  
  writer.println("Following is the stdout and stderr output of the sbt process started on ")
  
  def out(s: ⇒ String): Unit = {
    writer.println(s)
    print(".")  
  }
  
  def err(s: ⇒ String): Unit = {
    writer.println("<error> " + s)
    print("..error..")  
  }
  
  def buffer[T](f: => T): T = f  
}
