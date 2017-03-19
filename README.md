# AndroidTestingTools
A library of tools to make Android testing easier. 

## Download
Releases are made available through jCentre. Add `compile 'com.matthew-tamlin:android-testing-tools:2.0.0'` to your gradle build file to use the latest version. Older versions are available in the [maven repo](https://bintray.com/matthewtamlin/maven/AndroidTestingTools).

## Usage
This library contains the following tools:
- Test Harnesses
- EspressoHelper
- TypeSafeViewAction
- TypeSafeViewAssertion

### Test Harnesses
A test harness is an activity which displays a view along with as a series of controls for interacting with the view. Test harnesses facilitate manual testing by allowing direct interaction with the view, and they facilitate automated testing by providing a target for espresso tests.

A test harness can be created in three steps:
1. Subclass one of the provided test harnesses.
2. Define the test view by implementing getTestView().
3. Define the controls by annotating methods with @Control.

The example below displays a MyCustomView along with two buttons in a ControlsAboveViewTestHarness:
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

When converting only a single view:
```java
TextView textView = context.findViewById(R.id.my_text_view);
ViewInteraction interaction = EspressoHelper.viewToViewInteraction(textView);
```

When converting multiple views:
```
TextView tv1 = context.findViewById(R.id.my_text_view_1);
TextView tv2 = context.findViewById(R.id.my_text_view_2);
ImageView iv= context.findViewById(R.id.my_image_view);

ViewInteraction tvInteraction1 = EspressoHelper.viewToViewInteraction(textView1, "1");
ViewInteraction tvInteraction2 = EspressoHelper.viewToViewInteraction(textView2, "3.1415926");
ViewInteraction ivInteraction = EspressoHelper.viewToViewInteraction(imageView, "unique value");
```

### TypeSafeViewAction and TypeSafeViewAssertion
Usually writing custom espresso view actions and view assertions involves a considerable amount of type-checking boilerplate code. The TypeSafeViewAction and TypeSafeViewAssertion classes eliminate this annoyance. For example:
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

	public static TypeSafeViewAssertion<MyCustomView> getSomething(final int expectedValue) {
		return new TypeSafeViewAssertion<MyCustomView>(MyCustomView.class, true) {
			@Override
			public void typeSafeCheck(TextView view, NoMatchingViewException exception) {
				// Your view assertions here, for example:
				assertThat(view.getSomething(), is(expectedValue));
			}
		}
	}
}
```

### Putting it all together
All of these tools can be combined to simplify unit testing of custom views. For example:
```java
@RunWith(AndroidJUnit4.class)
public class TestMyCustomView {
	/*
	 * Disable the controls to avoid interfering with the expresso framework.
	 */
	@Rule
	public final ActivityTestRule<MyCustomViewTestHarness> activityRule =
		new ActivityTestRule<MyCustomViewTestHarness>(MyCustomViewTestHarness.class) {
			@Override
			protected void afterActivityLaunched() {
				super.afterActivityLaunched();
					
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().enableControls(false);
					}
				});
			}
		};
			
	private MyCustomView testViewDirect;
	
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
			.check(MyCustomViewActionsAndAssertions.getSomething(100));
		
	}
```

## Compatibility
This library is compatible with Android 12 and up. Android lint may warn about an InvalidPackage exception, however that shouldn't matter if the library is only used in testing. The warning can be suppressed by adding the following to the android scope in the relevant build.gradle file:
```
lintOptions {
	disable 'InvalidPackage'
}
 ```
