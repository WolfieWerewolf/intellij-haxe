/*
 * Copyright 2000-2013 JetBrains s.r.o.
 * Copyright 2014-2014 AS3Boyan
 * Copyright 2014-2014 Elias Ku
 * Copyright 2018 Eric Bishton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.plugins.haxe;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.haxe.build.ClassWrapper;
import com.intellij.plugins.haxe.build.IdeaTarget;
import com.intellij.plugins.haxe.build.MethodWrapper;
import com.intellij.plugins.haxe.util.HaxeDebugLogger;
import com.intellij.plugins.haxe.util.HaxeTestUtils;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.testFramework.PlatformTestCase;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;

/**
 * Created by fedorkorotkov.
 */
abstract public class HaxeCodeInsightFixtureTestCase extends JavaCodeInsightFixtureTestCase {
  @SuppressWarnings("JUnitTestCaseWithNonTrivialConstructors")
  protected HaxeCodeInsightFixtureTestCase() {
    super();
    HaxeDebugLogger.configurePrimaryLoggerToSwallowLogs();
  }

  @Override
  protected String getTestDataPath() {
    return HaxeTestUtils.BASE_TEST_DATA_PATH + getBasePath();
  }

  /**
   * Use reflection to load an annotator inspection class.
   * Specific to annotation tests, but placed here just to avoid adding yet another single-function base class.
   *
   * When we don't support versions of the plugin prior to v2016.1, we can revert the code to importing
   * the classes directly and get rid of this function.
   *
   * @return - An annotator-based inspection class instance.
   */
  protected InspectionProfileEntry getAnnotatorBasedInspection() {
    // Because we're loading an inner class, and Class.forName simply substitutes '.' for '/', it won't find
    // the class unless we use the '$' as the final path separator.  (Which is what the Java compiler does
    // when it  creates an inner class.)
    //String defaultInspectorClassName = IdeaTarget.IS_VERSTION_16_COMPATIBLE
    //                       ? "com.intellij.codeInsight.daemon.impl.DefaultHighlightVisitorBasedInspection$AnnotatorBasedInspection"
    //                       : "com.intellij.codeInspection.DefaultHighlightVisitorBasedInspection$AnnotatorBasedInspection";
    String defaultInspectorClassName = IdeaTarget.IS_VERSTION_16_COMPATIBLE
                                       ? "com.intellij.codeInsight.daemon.impl.DefaultHighlightVisitorBasedInspection"
                                       : "com.intellij.codeInspection.DefaultHighlightVisitorBasedInspection";

    ClassWrapper<InspectionProfileEntry> wrapper = new ClassWrapper<InspectionProfileEntry>(defaultInspectorClassName);
    ClassWrapper<InspectionProfileEntry> annotator = new ClassWrapper<InspectionProfileEntry>(wrapper, "AnnotatorBasedInspection");

    return annotator.newInstance();
  }

  public void setTestStyleSettings() {
    setTestStyleSettings(2);
  }

  public void setTestStyleSettings(int indent) {
    Project project = getProject();
    CodeStyleSettings currSettings = CodeStyleSettingsManager.getSettings(project);
    assertNotNull(currSettings);
    CodeStyleSettings tempSettings = currSettings.clone();
    CodeStyleSettings.IndentOptions indentOptions = tempSettings.getIndentOptions(HaxeFileType.HAXE_FILE_TYPE);
    indentOptions.INDENT_SIZE = indent;
    assertNotNull(indentOptions);
    CodeStyleSettingsManager.getInstance(project).setTemporarySettings(tempSettings);
  }
}
