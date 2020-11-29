package com.brambolt.wrench.steps

import com.brambolt.wrench.Target
import com.brambolt.wrench.runbooks.Step
import org.gradle.api.Task

/**
 * <p>This class supports syntactic sugar to create simple steps without direct
 * referrals to <code>org.gradle.api.DefaultTask</code>.</p>
 *
 * <p>Without this class, simple steps require an import statement:</p>
 * <pre>
 *   import org.gradle.api.DefaultTask
 *
 *   checkpoint c {
 *     steps {
 *       s1(type: DefaultTask) { ... }
 *     }
 *   }
 * </pre>
 *
 * <p>If the explicit type declaration is omitted, the <code>DefaultStep</code>
 * class is used instead and the syntax is slightly cleaner:</p>
 * <pre>
 *   checkpoint c {
 *     steps {
 *       s1 { ... }
 *     }
 *   }
 * </pre>
 */
class DefaultStep extends Step {

  DefaultStep(Map<String, ?> args, Target target, String name, Task task) {
    super(args, target, name, task)
  }
}
