package com.brambolt.wrench

import org.gradle.api.Project

import static java.util.Arrays.asList

class Wrenches {

  static Wrench find() {
    new Wrenches().findIn(new File('.'))
  }

  static Wrench find(Project project) {
    new Wrenches().findIn(project.projectDir)
  }

  Wrench findIn(File dir) {
    List<File> wrenches = findWrenches(dir)
    if (wrenches.isEmpty())
      notFound(dir)
    else if (1 < wrenches.size())
      tooMany(dir, wrenches)
    else
      fileWrench(wrenches.get(0))
  }

  List<File> findWrenches(File dir) {
    asList(dir.listFiles({ File file -> file.getName().endsWith('.wrench') } as FileFilter))
  }

  Wrench notFound(File dir) {
    new ExitWrench("No wrenches found in ${dir.absolutePath}")
  }

  Wrench tooMany(File dir, List<File> found) {
    new ExitWrench("Multiple wrenches found in ${dir.absolutePath}:\n\t${found.sort().join('\n\t')}")
  }

  Wrench fileWrench(File file) {
    new FileWrench(file)
  }
}

