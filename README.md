# AndroidTestingTools
A library of tools to make Android testing easier. 

**SUPPORT NOTICE: This library is now STABLE. It is no longer under active development, however pull requests from others are still being accepted.**

## Dependency
To use the library, add the following to your gradle build file:
```groovy
repositories {
  jcenter()
}

dependencies {
  implementation 'com.matthew-tamlin:android-testing-tools:3.0.2'
}

android {
  lintOptions {
    disable 'InvalidPackage'
  }
}
```

Older versions are available in the [maven repo](https://bintray.com/matthewtamlin/maven/AndroidTestingTools).

## Tools
This library contains the following tools:
- Test Harnesses
- EspressoHelper
- TypeSafeViewAction
- TypeSafeViewAssertion

### Test Harnesses
A test harness is an activity which displays a view along with as a series of controls for interacting with the view. Test harnesses facilitate manual testing by allowing direct interaction with the view, and they facilitate automated testing by providing a target for the espresso framework.

A test harness can be created in three steps:
1. Subclass one of the provided test harnesses.
2. Define the test view by implementing getTestView().
3. Define the controls by annotating methods with @Control.

The example below uses a ControlsAboveViewTestHarness to display a MyCustomView and two control buttons:
```java
public class MyCustomViewTestHarness extends ControlsAboveViewTestHarness<MyCustomView> {
  private MyCustomView testView;
	
  @Override
  public MyCustomView getTestView() {
    if (testView == null) {
      testView = new MyCustomView(this);
    }
		
    return testView();
  }
	
  @Control(1)
  public Button doSomething() {
    Button b = new Button(this);
    b.setText("Do something");
		
    b.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        getTestView().doSomething();
      }
    });
		
    return b;
  }
	
  @Control(2)
  public Button doSomethingElse() {
    Button b = new Button(this);
    b.setText("Do something else");
		
    b.setOnClickListener(new OnClickListener() {
      @Override
        public void onClick(View v) {
          getTestView().doSomethingElse();
        }
      });
		
      return b;
  }
}
```

Four test harnesses are provided in this library:
- ControlsAboveViewTestHarness: The controls are positioned vertically above the test view.
- ControlsBelowViewTestHarness: The controls are positioned vertically below the test view.
- ControlsOverViewTestHarness: The controls are stacked on top of the test view.
- EmptyTestHarness: Displays controls but no test view. Useful when an activity is needed in an automated test.

### EspressoHelper
The EspressoHelper class converts View objects to ViewInteraction objects. ViewInteration objects are necessary for espresso tests, since ViewActions and ViewAssertions cannot be applied to views directly. 

When converting only a single view, the viewToViewInteraction(View) method can be used.
```java
TextView textView = context.findViewById(R.id.my_text_view);
ViewInteraction interaction = EspressoHelper.viewToViewInteraction(textView);
```

When converting multiple views, the viewToViewInteraction(View, String) method must be used.
```
TextView tv1 = context.findViewById(R.id.my_text_view_1);
TextView tv2 = context.findViewById(R.id.my_text_view_2);
ImageView iv= context.findViewById(R.id.my_image_view);

ViewInteraction tvInteraction1 = EspressoHelper.viewToViewInteraction(textView1, "1");
ViewInteraction tvInteraction2 = EspressoHelper.viewToViewInteraction(textView2, "3.1415926");
ViewInteraction ivInteraction = EspressoHelper.viewToViewInteraction(imageView, "unique value");
```

### TypeSafeViewAction and TypeSafeViewAssertion
Usually writing custom espresso view actions and view assertions involves a considerable amount of type-checking and other boilerplate code. The TypeSafeViewAction and TypeSafeViewAssertion classes eliminate this annoyance.
```java
public class MyCustomViewActionsAndAssertions {
  public static TypeSafeViewAction<MyCustomView> doSomething(final int someArgument) {
    return new TypeSafeViewAction<MyCustomView>(MyCustomView.class, true) {
      @Override
      public void typeSafePerform(UiController uiController, MyCustomView view) {
        // Your view actions here, for example:
        view.doSomething(someArgument);
      }

      @Override
      public String getDescription() {
        return "do something";
      }
   }

  public static TypeSafeViewAssertion<MyCustomView> matchesSomething(final int expected) {
    return new TypeSafeViewAssertion<MyCustomView>(MyCustomView.class, true) {
      @Override
      public void typeSafeCheck(MyCustomView view, NoMatchingViewException exception) {
        // Your view assertions here, for example:
        assertThat(view.getSomething(), is(expected));
      }
    }
  }
}
```

The compiler will __not__ fail if a type safe action or assertion is used on an object with an invalid type, however runtime checks will automatically throw descriptive exceptions.

## Putting it all together
When used together, the tools in this library greatly simplify the process of testing custom views. To create an automated test suite for a custom view:
1. Create a test harness for the custom view.
2. Create view actions and view assertions (if the standard ones are not enough).
3. Create an Android JUnit test class where the test harness is launched using an ActivityTestRule.
4. Write the test cases.

Here is an example of an automated test for the MyCustomView class:
```java
@RunWith(AndroidJUnit4.class)
public class TestMyCustomView {
  @Rule
  public final ActivityTestRule<MyCustomViewTestHarness> activityRule =
    new ActivityTestRule<MyCustomViewTestHarness>(MyCustomViewTestHarness.class) {
      @Override
      protected void afterActivityLaunched() {
        super.afterActivityLaunched();
					
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            // Disable controls to ensure tests run smoothly
            getActivity().enableControls(false);
          }
        });
      }
    };
	
  // Useful for verifying callback arguments	
  private MyCustomView testViewDirect;

  // Can be used with the expresso framework
  private ViewInteraction testViewEspress;
	
  @Before
  public void setup() {
    testViewDirect = activityRule.getTestView();
    testViewEspresso = EspressoHelper.viewToViewInteraction(testViewDirect);
  }
	
  @Test
  public void testSomething() {
    testViewEspresso
        .perform(MyCustomViewActionsAndAssertions.doSomething(100))
        .check(MyCustomViewActionsAndAssertions.matchesSomething(100));
  }
}
```

## Compatibility
This library is compatible with Android 16 and up.
