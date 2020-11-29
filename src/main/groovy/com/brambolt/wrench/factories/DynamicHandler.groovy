package com.brambolt.wrench.factories

import com.brambolt.wrench.Target
import com.brambolt.wrench.WrenchException
import com.brambolt.wrench.finders.TaskClassFinder

/**
 * A dynamic handler provides node type-specific implementation to a dynamic
 * dispatch object.
 *
 * <p>The dispatch implements the node blocks of a wrench definition. This
 * means that when a wrench defines a sequence of nodes, such as runbook
 * checkpoints or steps, a dispatch object is responsible for processing the
 * node definitions. The dispatch relies on the handler for the actual
 * processing of the definitions. The dispatch is only responsible for
 * figuring out how to correctly delegate to its handler.</p>
 *
 * <p>For example, a runbook might define a checkpoint with two steps, like
 * this:</p>
 * <pre>
 *   checkpoint c1 {
 *     steps {
 *       s1
 *       s2(type: S)
 *       s3 { .. }
 *     }
 *   }
 * </pre>
 * <p>In this case the steps block is implemented by a dynamic dispatch with a
 * handler of type <code>StepSequenceBuilder</code>, which is a handler of type
 * <code>DynamicHandler&lt;Step&gt;</code>. The dispatch is responsible for
 * handling the <code>s1</code>, <code>s2</code> and <code>s3</code> step
 * declarations and does this by passing them to the step sequence builder.</p>
 *
 * @param <T> The node type the handler is responsible for
 */
trait DynamicHandler<T> {

  Target target

  /**
   * Either finds a class or a processes a node.
   *
   * <p>If the parameter <code>name</code> is capitalized then it is a type
   * specification and a class will be returned, if found.</p>
   *
   * <p>Else if the parameter <code>name</code> starts with a lower case letter
   * then it is treated as a node name.<p>
   *
   * @param name The class or node name
   * @return The class or node with the parameter name
   * @throws WrenchException If unable to locate the class or node
   */
  Object findOrProcess(String name) {
    if (name.getChars()[0].isUpperCase())
      find(name)
    else process(name, {})
  }

  /**
   * Looks up a task class with the parameter <code>simpleName</code>.
   * @param simpleName The unqualified name of the class to find
   * @return The task class with the parameter <code>simpleName</code>
   * @throws WrenchException If unable to find the task class
   */
  Object find(String simpleName) {
    new TaskClassFinder(target).chooseTaskClass(simpleName, simpleName)
  }

  T process(String name, Closure closure) {
    process([:], name, closure)
  }

  T process(Map<String, Object> args, String name) {
    process(args, name, {})
  }

  abstract T process(Map<String, Object> args, String name, Closure closure)
}