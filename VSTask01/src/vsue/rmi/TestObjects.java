package vsue.rmi;

import java.io.Serializable;
import java.util.Comparator;

public class TestObjects {

    public static final Comparator<Integer> intComparer = new Comparator<Integer>() {
        @Override
        public int compare(Integer arg0, Integer arg1) {
            return arg0.compareTo(arg1);
        }
    };
    public static final Comparator<String> strComparer = new Comparator<String>(){
        @Override
        public int compare(String arg0, String arg1) {
            return arg0.compareTo(arg1);
        }
    };
    
    public static TestObject<Integer> wrappedInteger = new TestObject<Integer>(12341, intComparer);
    public static TestObject<String> shortString = new TestObject<String>("hallo welt", strComparer);
    public static TestObject<String> veryLongString = new TestObject<String>("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam volutpat mattis justo et scelerisque. Quisque lacinia rutrum dolor. Sed eget elementum elit. Sed ac metus et odio iaculis mollis. Pellentesque vel lorem semper, accumsan ex a, dapibus est. Duis at placerat lacus. Cras auctor ex vitae faucibus eleifend.Sed et ultrices lectus. Mauris vehicula nulla at dui maximus congue. Nunc placerat elementum ligula, eget gravida odio tincidunt nec. Ut euismod non nulla sed gravida. Donec dictum, mauris quis ultricies placerat, arcu metus accumsan dui, et porta nisi orci et nulla. Proin fringilla ornare massa nec efficitur. Aenean venenatis augue id nulla congue placerat.Proin malesuada faucibus dolor in vulputate. Praesent turpis nulla, pretium sed erat eget, interdum iaculis justo. Etiam suscipit lacinia diam nec feugiat. Pellentesque et sagittis metus, a volutpat justo. Phasellus eleifend, nunc scelerisque pretium hendrerit, sapien risus consequat est, eu finibus nisi velit hendrerit arcu. Duis lacinia blandit quam non mollis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec accumsan sem eu finibus maximus.Donec varius sapien non ligula facilisis consequat vestibulum sed velit. Morbi auctor augue a ex blandit finibus. Aliquam congue, enim id feugiat mollis, ipsum nibh viverra lectus, sit amet facilisis dui sem eu quam. Suspendisse convallis, dolor sit amet blandit maximus, elit sapien efficitur turpis, non mollis leo ipsum ac est. Ut in molestie ante, ut tempor orci. Cras elementum sem a efficitur iaculis. Suspendisse ut euismod odio. Vivamus et magna quis nunc mollis tempor id fermentum nisl. Duis suscipit tristique ante sit amet efficitur.Pellentesque posuere viverra mauris, vitae congue ipsum hendrerit sit amet. Integer laoreet nisl a tempor porta. Duis vitae nisi quis erat auctor pretium ut quis libero. Donec id posuere tellus. Cras vestibulum turpis quis neque convallis, at feugiat orci pellentesque. Proin at justo non nibh euismod bibendum. Donec molestie est quis est pharetra, at pulvinar nisi finibus. Curabitur tristique turpis vitae sodales sodales.Maecenas mauris magna, tempus at odio ut, accumsan rutrum purus. Sed in sollicitudin leo. Nulla elit orci, elementum in lobortis nec, sagittis vitae nunc. Integer pretium enim ut sapien dapibus, rhoncus dictum felis blandit. Quisque ullamcorper maximus eros at semper. Etiam mollis blandit est sed varius. Quisque pretium ex in nisl sodales dapibus. Ut et est eleifend, semper felis ac, mattis elit. Nunc quis mi faucibus, dictum nisl eu, posuere dolor. Lorem ipsum dolor sit amet, consectetur adipiscing elit.Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Phasellus eget facilisis augue. Donec vulputate, arcu nec pretium gravida, justo neque gravida ligula, id pretium ligula ex vitae lacus. Phasellus mollis pulvinar sem, ac convallis ex accumsan convallis. Duis tincidunt tellus vel rhoncus tincidunt. Sed mattis euismod nulla. Sed sodales, mi eget ornare tempor, nisi nunc molestie orci, sed faucibus nulla ligula eu felis. Donec consequat at diam ac mattis. Nullam finibus tincidunt lacinia. Aliquam venenatis libero neque, sed tincidunt magna semper at.Nunc eu lacinia turpis. Aenean vel sollicitudin felis. Praesent venenatis vestibulum purus, at cursus quam vehicula nec. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Aenean augue massa, hendrerit eu est id, hendrerit posuere dolor. In lobortis fermentum odio, et euismod nunc. Nulla ut orci lacus. Proin sollicitudin, eros ac placerat interdum, velit tellus euismod risus, ac feugiat erat purus id mi. Donec molestie faucibus porttitor. Sed nec sem eu nibh eleifend tempus feugiat at odio. Morbi eu metus a dolor aliquam tempor. Vestibulum varius libero commodo tellus varius faucibus. Suspendisse blandit ipsum a orci luctus, sit amet molestie ipsum vestibulum.Nulla facilisi. Mauris ornare sit amet lectus feugiat pretium. Vestibulum rhoncus accumsan orci, eget finibus leo aliquet ut. Curabitur nec lorem tempor, egestas nisi id, ultricies mauris. Morbi id semper turpis. Suspendisse eleifend metus semper rutrum placerat. Duis aliquam hendrerit tempus. Suspendisse non pellentesque nibh, nec pulvinar nisi. Etiam a ante a lorem cursus aliquam in sed arcu. Fusce in ultrices eros. Aliquam dapibus suscipit diam, in accumsan elit accumsan vel. Fusce fermentum semper metus nec sagittis.Praesent placerat molestie luctus. Etiam faucibus mauris ut vulputate aliquam. Fusce faucibus cursus orci, eu aliquet est. Vivamus luctus nulla eu dolor facilisis, faucibus tempor mi consequat. Sed sed tempor augue. Aenean arcu felis, accumsan a justo consectetur, volutpat molestie felis. Nulla eu turpis sed urna vehicula interdum ac eu justo. Pellentesque dui tortor, accumsan sit amet diam ut, volutpat luctus magna. Aliquam nec dolor bibendum, vehicula lacus convallis, sagittis felis. Ut venenatis venenatis ex. Nam in sed.", strComparer);
    
    public static class TestObject<T extends Serializable> {
        public T obj;
        public Comparator<T> comparator = null;
        
        public TestObject(T obj, Comparator<T> comparator){
            this.obj = obj;
            this.comparator = comparator;
        }
        
        public boolean objIsEqualTo(T other){

            return comparator.compare(obj, other) == 0;
        }
    }

}
