package org.yaml.snakeyaml.representer;

/**
 * 提供空参构造，修复项目不能启动（复制到每个项目中）
 *
 * @author bydud
 * @since 2024/5/21 8:51
 */

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.*;

import java.util.*;

public class Representer extends SafeRepresenter {
    protected Map<Class<? extends Object>, TypeDescription> typeDefinitions = Collections.emptyMap();

    public Representer() {
        super(new DumperOptions());
        this.representers.put(null, new RepresentJavaBean());
    }

    public Representer(DumperOptions options) {
        super(options);
        this.representers.put(null, new RepresentJavaBean());
    }

    public TypeDescription addTypeDescription(TypeDescription td) {
        if (Collections.EMPTY_MAP == this.typeDefinitions) {
            this.typeDefinitions = new HashMap();
        }

        if (td.getTag() != null) {
            this.addClassTag(td.getType(), td.getTag());
        }

        td.setPropertyUtils(this.getPropertyUtils());
        return (TypeDescription) this.typeDefinitions.put(td.getType(), td);
    }

    public void setPropertyUtils(PropertyUtils propertyUtils) {
        super.setPropertyUtils(propertyUtils);
        Collection<TypeDescription> tds = this.typeDefinitions.values();
        Iterator var3 = tds.iterator();

        while (var3.hasNext()) {
            TypeDescription typeDescription = (TypeDescription) var3.next();
            typeDescription.setPropertyUtils(propertyUtils);
        }

    }

    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        List<NodeTuple> value = new ArrayList(properties.size());
        Tag customTag = (Tag) this.classTags.get(javaBean.getClass());
        Tag tag = customTag != null ? customTag : new Tag(javaBean.getClass());
        MappingNode node = new MappingNode(tag, value, FlowStyle.AUTO);
        this.representedObjects.put(javaBean, node);
        FlowStyle bestStyle = FlowStyle.FLOW;
        Iterator var8 = properties.iterator();

        while (true) {
            NodeTuple tuple;
            do {
                if (!var8.hasNext()) {
                    if (this.defaultFlowStyle != FlowStyle.AUTO) {
                        node.setFlowStyle(this.defaultFlowStyle);
                    } else {
                        node.setFlowStyle(bestStyle);
                    }

                    return node;
                }

                Property property = (Property) var8.next();
                Object memberValue = property.get(javaBean);
                Tag customPropertyTag = memberValue == null ? null : (Tag) this.classTags.get(memberValue.getClass());
                tuple = this.representJavaBeanProperty(javaBean, property, memberValue, customPropertyTag);
            } while (tuple == null);

            if (!((ScalarNode) tuple.getKeyNode()).isPlain()) {
                bestStyle = FlowStyle.BLOCK;
            }

            Node nodeValue = tuple.getValueNode();
            if (!(nodeValue instanceof ScalarNode) || !((ScalarNode) nodeValue).isPlain()) {
                bestStyle = FlowStyle.BLOCK;
            }

            value.add(tuple);
        }
    }

    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        ScalarNode nodeKey = (ScalarNode) this.representData(property.getName());
        boolean hasAlias = this.representedObjects.containsKey(propertyValue);
        Node nodeValue = this.representData(propertyValue);
        if (propertyValue != null && !hasAlias) {
            NodeId nodeId = nodeValue.getNodeId();
            if (customTag == null) {
                if (nodeId == NodeId.scalar) {
                    if (property.getType() != Enum.class && propertyValue instanceof Enum) {
                        nodeValue.setTag(Tag.STR);
                    }
                } else {
                    if (nodeId == NodeId.mapping && property.getType() == propertyValue.getClass() && !(propertyValue instanceof Map) && !nodeValue.getTag().equals(Tag.SET)) {
                        nodeValue.setTag(Tag.MAP);
                    }

                    this.checkGlobalTag(property, nodeValue, propertyValue);
                }
            }
        }

        return new NodeTuple(nodeKey, nodeValue);
    }

    protected void checkGlobalTag(Property property, Node node, Object object) {
        if (object.getClass().isArray() && object.getClass().getComponentType().isPrimitive()) {
            return;
        }

        Class<?>[] arguments = property.getActualTypeArguments();
        if (arguments != null) {
            if (node.getNodeId() == NodeId.sequence) {
                // apply map tag where class is the same
                Class<? extends Object> t = arguments[0];
                SequenceNode snode = (SequenceNode) node;
                Iterable<Object> memberList = Collections.emptyList();
                if (object.getClass().isArray()) {
                    memberList = Arrays.asList((Object[]) object);
                } else if (object instanceof Iterable<?>) {
                    // list
                    memberList = (Iterable<Object>) object;
                }
                Iterator<Object> iter = memberList.iterator();
                if (iter.hasNext()) {
                    for (Node childNode : snode.getValue()) {
                        Object member = iter.next();
                        if (member != null) {
                            if (t.equals(member.getClass())) {
                                if (childNode.getNodeId() == NodeId.mapping) {
                                    childNode.setTag(Tag.MAP);
                                }
                            }
                        }
                    }
                }
            } else if (object instanceof Set) {
                Class<?> t = arguments[0];
                MappingNode mnode = (MappingNode) node;
                Iterator<NodeTuple> iter = mnode.getValue().iterator();
                Set<?> set = (Set<?>) object;
                for (Object member : set) {
                    NodeTuple tuple = iter.next();
                    Node keyNode = tuple.getKeyNode();
                    if (t.equals(member.getClass())) {
                        if (keyNode.getNodeId() == NodeId.mapping) {
                            keyNode.setTag(Tag.MAP);
                        }
                    }
                }
            } else if (object instanceof Map) { // NodeId.mapping ends-up here
                Class<?> keyType = arguments[0];
                Class<?> valueType = arguments[1];
                MappingNode mnode = (MappingNode) node;
                for (NodeTuple tuple : mnode.getValue()) {
                    resetTag(keyType, tuple.getKeyNode());
                    resetTag(valueType, tuple.getValueNode());
                }
            } else {
                // the type for collection entries cannot be
                // detected
            }
        }

    }

    private void resetTag(Class<? extends Object> type, Node node) {
        Tag tag = node.getTag();
        if (tag.matches(type)) {
            if (Enum.class.isAssignableFrom(type)) {
                node.setTag(Tag.STR);
            } else {
                node.setTag(Tag.MAP);
            }
        }

    }

    protected Set<Property> getProperties(Class<? extends Object> type) {
        return this.typeDefinitions.containsKey(type) ? ((TypeDescription) this.typeDefinitions.get(type)).getProperties() : this.getPropertyUtils().getProperties(type);
    }

    protected class RepresentJavaBean implements Represent {
        protected RepresentJavaBean() {
        }

        public Node representData(Object data) {
            return Representer.this.representJavaBean(Representer.this.getProperties(data.getClass()), data);
        }
    }
}