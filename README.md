# JSON Manager

This is a library used to managing the exporting and importing of JSON files.

### What is JSON?

JSON is short for JavaScript Object Notation. It's a file structure written in plain text in files with ".json" extensions. Something like "somefile.json". You can open it up with any text editor. The structure looks something like this:

```
{
    "name": "Phineas"
    "age": 9
    "love interest": {
        "name": "Isabella"
        "age": 9
    }
    "friends": ["Ferb", "Baljeet", "Buford"]
    "pet": {
        "name": "Perry"
        "species": "Platypus"
        "nemesis": "Dr. Heinz Doofenshmirtz"
    }
}
```

There are other famous file types like this too. One is "XML" and the other is "YAML".

Any of those are great, but here we're going to stick with JSON.

### Why is this a problem?

JSON is great. 

1) It allows you to exchange objects between programs easily.
2) Almost all programming languages have some kind of serialization to pass objects from one to the other. But serialization is dangerous. If the object is modified then it's unclear what it means to deserialized into the modified object. And serialization is just a quick and dirty way to move objects around. To really develop it in a robust way, you need to transfer the essence of an object into a file type, be it XML, JSON or YAML. But doing this by hand for every object might be tedious, and other libraries for creating JSON can only go so far, like this: https://stackoverflow.com/questions/8876089/how-to-fluently-build-json-in-java

### How to use this library

There are 3 main objects: JSONConstantsBase, JSONImporter and JSONExporter.

Let's say your object to import and export is called "AbstractProperty". Inside you define 3 other classes that looks like this:

#### JSONConstants:
```
    private static class JSONConstants extends JSONConstantsBase {
        private static final String ABSTRACT_PROPERTY = "AbstractProperty";
        private static final String CHILD_ABSTRACT_PROPERTIES = "Child AbstractProperties";
        private static final String UNIT = "Unit";
        private static final String UNIT_VERSION = "Unit version index";
    }
```

Inside JSONConstantsBase, variables `TYPE` is already defined. `TYPE` is the key for the name of your object. Inside JSONConstants you can define the type name, like `String ABSTRACT_PROPERTY = "AbstractProperty";` in our example. You can also define other keys to be used later on.

#### AbstractPropertyJSONImporter
```
    private static class AbstractPropertyJSONImporter extends JSONImporter {
        @Override
        public Object generateObject(@NonNull JSONObject jsonObject) {
            try {
                AbstractProperty answer; // here you define the object you are going to return.
                if (!jsonObject.getString(JSONConstants.TYPE).equals(JSONConstants.ABSTRACT_PROPERTY)) {
                    throw new ParsingWrongObjectTypeException(); // here you check the type. If it is not the same as this object then throw some error
                }
                String description = jsonObject.getString(JSONConstants.DESCRIPTION);


                // this part you reconstruct the object however you want
                if (!jsonObject.has(JSONConstants.CHILD_ABSTRACT_PROPERTIES)) {
                    answer = new AbstractProperty(description, Objects.requireNonNull(Unit.Defaults.get(jsonObject.getString(JSONConstants.UNIT))));
                } else {
                    answer = new AbstractProperty(description);
                    JSONArray array = jsonObject.getJSONArray(JSONConstants.CHILD_ABSTRACT_PROPERTIES);
                    for (int i = 0; i < array.length(); i++) {
                        AbstractProperty childAbstractProperty = (AbstractProperty) AbstractProperty.getJSONImporter().generateObject(array.getJSONObject(i));
                        answer.addChildAbstractProperty(childAbstractProperty);
                    }
                }
                if (jsonObject.has(JSONConstants.UNIT)) {
                    Unit unit = Unit.Defaults.get(jsonObject.getString(JSONConstants.UNIT));
                    if (answer.getUnit() != unit) {
                        throw new AssertionError();
                    }
                    answer.setUnitVersionByIndex(jsonObject.getInt(JSONConstants.UNIT_VERSION));
                }



                return answer;
            } catch (JSONException e) {
                e.printStackTrace();
                return new AbstractProperty("");
            }
        }
    }
```

Here, to implement JSONImporter, you must implement `Object generateObject(JSONObject jsonObject)`, meaning here you define how the object is going to be constructed from a JSONObject. A good practice is to check whether the type of the object from JSONObject is the same as the type of this object. If yes then continue on your object construction.

#### AbstractPropertyJSONExporter

```
    private class AbstractPropertyJSONExporter extends JSONExporter {
        @Override
        public String generateJSON(@NonNull StringBuilder stringBuilder) {
            stringBuilder.append("{");
            stringBuilder.append("\"").append(JSONConstants.TYPE).append("\": \"").append(JSONConstants.ABSTRACT_PROPERTY).append("\", ");
            stringBuilder.append("\"").append(JSONConstants.DESCRIPTION).append("\": \"").append(getDescription()).append("\", ");
            if (treeSize() > 0) {
                stringBuilder.append("\"").append(JSONConstants.CHILD_ABSTRACT_PROPERTIES).append("\": [");
                for (int i = 0; i < childTrees.size(); i++) {
                    ((AbstractProperty) childTrees.get(i)).getJSONExporter().generateJSON(stringBuilder);
                    if (i < childTrees.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                stringBuilder.append("]");
                if (amountEditable()) {
                    stringBuilder.append(", ");
                }
            }
            if (amountEditable()) {
                stringBuilder.append("\"").append(JSONConstants.UNIT).append("\": \"").append(unit.read().getDescription()).append("\", ");
                stringBuilder.append("\"").append(JSONConstants.UNIT_VERSION).append("\": \"").append(unitVersionIndex.getNonNull()).append("\"");
            }
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }
```

You can then define how you are going to export your object. Given a `StringBuilder` object, you have to construct a JSON string to append to that stringBuilder.

#### Other bits of code

In `AbstractProperty`, you must have several other key elements:

```
class AbstractProperty {
    //JSON managers
    private static AbstractPropertyJSONImporter jsonImporter = new AbstractPropertyJSONImporter();
    private AbstractPropertyJSONExporter jsonExporter = new AbstractPropertyJSONExporter();


    public static JSONImporter getJSONImporter() {
        return jsonImporter;
    }

    public JSONExporter getJSONExporter() {
        return jsonExporter;
    }
}
```

After defining how to move back and forth between your real object and JSON text, you can import an object from a file:

```
AbstractProperty recoveredObject = (AbstractProperty) AbstractProperty.getJSONImporter().importJSON(fileName);
```

You can import an object from a `JSONObject` object:

```
AbstractProperty recoveredObject = (AbstractProperty) AbstractProperty.getJSONImporter().generateObject(jsonObject);
```

You can export an object to a file:

```
AbstractProperty ap = ...;
ap.getJSONExporter().exportJSON(fileName);
```

You can obtain a String of the JSON text:

```
AbstractProperty ap = ...;
String JSONText = ap.getJSONExporter().generateJSON();
```

If you have 2 objects, one of class `A` and one of class `B`. `a` is an instance of class `A` and inside `a`, there is an instance `b` of class `B`. If you have defined how to export `B`, you can export `A` like this:

```
class A {
    private class AJSONExporter extends JSONExporter {
        B b = ...;

        @Override
        public String generateJSON(@NonNull StringBuilder stringBuilder) {
            stringBuilder.append("{");

            // add some stuff to stringBuilder

            b.getJSONExporter().generateJSON(stringBuilder); // builds JSON text of b on top of a

            // add some more stuff to stringBuilder

            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }
}
```

### How can this be improved?

In JSON text generation, other libraries can be used to ease up life because then you don't need to care about the individual quotation marks, colons and curly braces. That can work, but we want to have some kind of flexibility as we are already dependent on https://mvnrepository.com/artifact/org.json/json/20140107

