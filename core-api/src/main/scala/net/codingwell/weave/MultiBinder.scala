//Apache license

package net.codingwell.weave

import com.google.common.collect.ImmutableSet
import com.google.inject._
import com.google.inject.spi._
import com.google.inject.multibindings._
import java.lang.annotation.Annotation
import java.util.{Set => JSet}

import scala.collection.JavaConversions._
import scala.collection.{ mutable => mu, immutable => im }

object ScalaMultibinder {
  class SetProvider[T] (val source:Key[JSet[T]]) extends ProviderWithDependencies[im.Set[T]] {

    @Inject() var injector:Injector = null

    def get() = {
      asScalaSet( injector.getInstance( source ) ).toSet[T]
    }

    def getDependencies() = {
      ImmutableSet.of( Dependency.get( source ) )
    }
  }

import java.lang.reflect.Type
    def typeOf[T](implicit m: Manifest[T]): Type = {
        def toWrapper(c:Type) = c match {
            case java.lang.Byte.TYPE => classOf[java.lang.Byte]
            case java.lang.Short.TYPE => classOf[java.lang.Short]
            case java.lang.Character.TYPE => classOf[java.lang.Character]
            case java.lang.Integer.TYPE => classOf[java.lang.Integer]
            case java.lang.Long.TYPE => classOf[java.lang.Long]
            case java.lang.Float.TYPE => classOf[java.lang.Float]
            case java.lang.Double.TYPE => classOf[java.lang.Double]
            case java.lang.Boolean.TYPE => classOf[java.lang.Boolean]
            case java.lang.Void.TYPE => classOf[java.lang.Void]
            case cls => cls
        }
        import com.google.inject.util.Types
        m.typeArguments match {
            case Nil => toWrapper(m.erasure)
            case args => m.erasure match {
                case c:Class[_] if c.getEnclosingClass == null => Types.newParameterizedType(c, args.map(typeOf(_)):_*)
                case c:Class[_] => Types.newParameterizedTypeWithOwner(c.getEnclosingClass, c, args.map(typeOf(_)):_*)
            }
        }
    }
 
  def typeLiteral[T : Manifest]: TypeLiteral[T] = {
    TypeLiteral.get(typeOf[T]).asInstanceOf[TypeLiteral[T]]
  }

  /**
   * Returns a new multibinder that collects instances of {@code type} in a {@link Set} that is
   * itself bound with no binding annotation.
   */
  def newSetBinder[T : Manifest]( binder:Binder, settype:TypeLiteral[T] ):Multibinder[T] = {
    val mybinder = binder.skipSources( classOf[ScalaMultibinder] )
    val result = Multibinder.newSetBinder( mybinder, settype )
    binder.bind( Key.get( typeLiteral[im.Set[T]] ) ).toProvider( new SetProvider[T]( Key.get( typeLiteral[JSet[T]] ) ) )
    result
  }

  def newSetBinder[T : Manifest]( binder:Binder, settype:Class[T], annotation:Annotation ):Multibinder[T] = {
    val mybinder = binder.skipSources( classOf[ScalaMultibinder] )
    val result = Multibinder.newSetBinder( mybinder, settype, annotation )
    binder.bind( Key.get( typeLiteral[im.Set[T]], annotation) ).toProvider( new SetProvider[T]( Key.get( typeLiteral[JSet[T]], annotation ) ) )
    result
  }
  /**
  public static <T> Multibinder<T> newSetBinder(Binder binder, Class<T> type) {
    return newSetBinder(binder, TypeLiteral.get(type));
  }

  public static <T> Multibinder<T> newSetBinder(
      Binder binder, TypeLiteral<T> type, Annotation annotation) {
    binder = binder.skipSources(RealMultibinder.class, Multibinder.class);
    RealMultibinder<T> result = new RealMultibinder<T>(binder, type,
        Key.get(Multibinder.<T>setOf(type), annotation));
    binder.install(result);
    return result;
  }

   * Returns a new multibinder that collects instances of {@code type} in a {@link Set} that is
   * itself bound with {@code annotation}.
  public static <T> Multibinder<T> newSetBinder(
      Binder binder, Class<T> type, Annotation annotation) {
    return newSetBinder(binder, TypeLiteral.get(type), annotation);
  }

   * Returns a new multibinder that collects instances of {@code type} in a {@link Set} that is
   * itself bound with {@code annotationType}.
  public static <T> Multibinder<T> newSetBinder(Binder binder, TypeLiteral<T> type,
      Class<? extends Annotation> annotationType) {
    binder = binder.skipSources(RealMultibinder.class, Multibinder.class);
    RealMultibinder<T> result = new RealMultibinder<T>(binder, type,
        Key.get(Multibinder.<T>setOf(type), annotationType));
    binder.install(result);
    return result;
  }

   * Returns a new multibinder that collects instances of {@code type} in a {@link Set} that is
   * itself bound with {@code annotationType}.
  public static <T> Multibinder<T> newSetBinder(Binder binder, Class<T> type,
      Class<? extends Annotation> annotationType) {
    return newSetBinder(binder, TypeLiteral.get(type), annotationType);
  } 
*/
}

class ScalaMultibinder() {
}
