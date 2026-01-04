package dev.bongballe.libs.screenshot

import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import java.io.File
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class BongBalleRoborazziRule(
  roborazziOptions: RoborazziOptions = RoborazziOptions(
    compareOptions = RoborazziOptions.CompareOptions(
      imageComparator = SimpleImageComparator(maxDistance = 0.007F, hShift = 2, vShift = 2),
    ),
  ),
) : TestRule {

  @OptIn(ExperimentalRoborazziApi::class)
  private val roborazziRule = RoborazziRule(
    options = RoborazziRule.Options(
      outputDirectoryPath = "src/test/screenshots",
      outputFileProvider = { description, outputDirectory, fileExtension ->
        File(
          outputDirectory,
          "${description.testClass.name}.${description.methodName}.$fileExtension",
        )
      },
      roborazziOptions = roborazziOptions,
    ),
  )

  override fun apply(base: Statement, description: Description): Statement = roborazziRule.apply(base, description)
}
