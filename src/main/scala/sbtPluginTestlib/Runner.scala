package org.canve.sbtPluginTestLib

import org.canve.util.CanveDataIO
import java.io.{File}
import scala.sys.process._

/*
 * Runs canve for each project included under the designated directory, 
 * by adding the canve sbt plugin to the project's sbt definition   
 */
object Runner extends App {
  
  val testProjectsRoot = "test-projects"
  
  val results = CanveDataIO.getSubDirectories(testProjectsRoot) map { projectDirObj =>
    val project = Project(projectDirObj, projectDirObj.getName)
    
    val projectPath = testProjectsRoot + File.separator + project.name 
    print("\n" + Console.YELLOW + Console.BOLD + s"Running the sbt plugin for $projectPath..." + Console.RESET) 
    val result = injectAndTest(project)
    println(result match {
      case true => "finished okay"
      case false => "failed"
    })
    
    Result(project, result)
  } 
  
  Summary(results)
  
  private def injectAndTest(project: Project): Boolean = {
    // add the plugin to the project's sbt setup
    scala.tools.nsc.io.File(project.dirObj.toString + File.separator + "project/canve.sbt")
      .writeAll("""addSbtPlugin("canve" % "sbt-plugin" % "0.0.1")""" + "\n")      
     
    // run sbt for the project and get the exit code
    Process(Seq("sbt", "-Dsbt.log.noformat=true", "canve"), project.dirObj) ! (new FilteringOutputWriter(RedirectionMapper(project))) == 0
  }
}
