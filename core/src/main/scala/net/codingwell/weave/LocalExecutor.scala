
package net.codingwell.weave
import com.google.inject.Inject

class LocalExecutor @Inject() (val compilers:Seq[Compiler]) extends Executor {

}
