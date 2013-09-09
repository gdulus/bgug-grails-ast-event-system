package test.system.events.ast.transformation;

import groovyjarjarasm.asm.Opcodes;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import test.system.events.SystemEvent;
import test.system.events.SystemEventDispatcher;
import test.system.events.SystemEventListener;

import java.util.Map;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
final public class OnSystemEventASTTransformation implements ASTTransformation {

    private static final String MAPPING_FIELD_NAME = "SYSTEM_EVENTS_MAPPING";

    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        if (!isValid(astNodes)) {
            System.out.println("[OnSystemEvent]: invalid node, leave.");
        }
        // ---------------------------------------------------------------------
        // 1) GET BASIC DATA
        //
        final MethodNode methodNode = (MethodNode) astNodes[1];                 // annotated method
        final ClassNode declaringClass = methodNode.getDeclaringClass();        // class where the annotated method is declared
        final Parameter[] parameters = methodNode.getParameters();              // list of parameters of the annotated method
        final String eventFullName = parameters[0].getOriginType().getName();   // full name of a event class
        // just some logging ...
        final String methodName = methodNode.getName();
        final String callbackFullName = declaringClass.getName() + "#" + methodName + "(" + parameters[0].getType() + ")";
        System.out.println("[OnSystemEvent]: registering " + callbackFullName + ") listener");
        // ---------------------------------------------------------------------
        // 2) GET MAPPING FIELD, IF NOT EXISTS CREATE IT
        //
        final FieldNode mapping = getOrCreateMappingField(declaringClass);
        // ---------------------------------------------------------------------
        // 3) ADD TO MAPPING FIELD KEY = FULL NAME OF THE EVENT, VALUE = NAME OF THE CALLBACK METHOD
        //
        ((MapExpression) mapping.getInitialValueExpression()).addMapEntryExpression(new ConstantExpression(eventFullName), new ConstantExpression(methodName));
        // ---------------------------------------------------------------------
        // 4) IMPLEMENT SystemEventListener INTERFACE IF NOT EXISTS
        //
        implementSystemEventListenerInterfaceIfNotExists(declaringClass, mapping);
    }

    private boolean isValid(ASTNode[] astNodes) {
        return (astNodes != null
            && astNodes[0] != null
            && astNodes[1] != null
            && astNodes[1] instanceof MethodNode
            && astNodes[0] instanceof AnnotationNode);
    }

    private FieldNode getOrCreateMappingField(final ClassNode classNode) {
        FieldNode mappingField = classNode.getDeclaredField(MAPPING_FIELD_NAME);

        if (mappingField == null) {
            mappingField = new FieldNode(MAPPING_FIELD_NAME,
                Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC,
                getGenericStringStringMap(),
                classNode,
                new MapExpression());

            classNode.addField(mappingField);
        }

        return mappingField;
    }

    private void implementSystemEventListenerInterfaceIfNotExists(final ClassNode classNode, final FieldNode mapping) {
        final ClassNode systemEventListenerInterface = new ClassNode(SystemEventListener.class);

        if (!classNode.declaresInterface(systemEventListenerInterface)) {
            // implement interface
            classNode.addInterface(systemEventListenerInterface);
            // implement interface methods (void onApplicationEvent(SystemEvent event))
            final Parameter interfaceMethodParameter = new Parameter(new ClassNode(SystemEvent.class), "event");
            final ArgumentListExpression dispatcherParameters = new ArgumentListExpression(VariableExpression.THIS_EXPRESSION, new VariableExpression("event"));
            final StaticMethodCallExpression dispatcherCall = new StaticMethodCallExpression(new ClassNode(SystemEventDispatcher.class), "dispatch", dispatcherParameters);
            final BlockStatement methodBody = new BlockStatement(new Statement[]{new ExpressionStatement(dispatcherCall)}, new VariableScope());
            classNode.addMethod("onApplicationEvent",
                Opcodes.ACC_PUBLIC,
                ClassHelper.VOID_TYPE,
                new Parameter[]{interfaceMethodParameter},
                null,
                methodBody);
            // implement interface methods (public Map<String, String> getMapping())
            classNode.addMethod("getMapping",
                Opcodes.ACC_PUBLIC,
                getGenericStringStringMap(),
                Parameter.EMPTY_ARRAY,
                null,
                new ReturnStatement(new FieldExpression(mapping)));
        }
    }

    private ClassNode getGenericStringStringMap() {
        final ClassNode map = new ClassNode(Map.class);
        map.setUsingGenerics(true);
        final GenericsType stringGenericTypeKey = new GenericsType(new ClassNode(String.class));
        final GenericsType stringGenericTypeValue = new GenericsType(new ClassNode(String.class));
        final GenericsType[] genericsTypes = new GenericsType[]{stringGenericTypeKey, stringGenericTypeValue};
        map.setGenericsTypes(genericsTypes);
        return map.getPlainNodeReference();
    }
}

