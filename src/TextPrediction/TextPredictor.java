package TextPrediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JOptionPane;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;


public class TextPredictor {
	
	public static void main(String[] args) {
		
		TextPredictor predictor = new TextPredictor();
		  
	     Scanner input = new Scanner(System.in);
	     System.out.print("Enter the path to the training library: ");
	     String library = input.nextLine();
	     predictor.train(library);

	     System.out.print("Enter the sentence to predict the next words: ");
	     String sentence = input.nextLine();
	  
	     System.out.print("Enter the number of words to predict: ");
	     int numWords = input.nextInt();

	     String[] predictedWords = predictor.predict(sentence, numWords, numWords);
	     System.out.println("Predicted words: " + String.join(" ", predictedWords));

	     input.close();
	    }
	
	  private Map<String, Map<String, Double>> transducer = new HashMap<>();
	  private Random random = new Random();
	  
	  // This is where the program generates states/transitions and assign weights based on the weighted finite state transducer
	  public void addTransition(String currentState, String nextState, double weight) {
	    if (!transducer.containsKey(currentState)) {
	      transducer.put(currentState, new HashMap<>());
	    }
	    Map<String, Double> nextStates = transducer.get(currentState);
	    nextStates.put(nextState, nextStates.getOrDefault(nextState, 0.0) + weight);
	  }
	  
	 // Calculations and normalizations of weights, ensures that the sum of the weights of all outgoing transitions from a state is equal to 1
	  public void normalizeWeights() {
		  
	    for (Map<String, Double> nextStates : transducer.values()) {
	      double totalWeight = 0.0;
	      for (double weight : nextStates.values()) {
	        totalWeight += weight;
	      }
	      for (Map.Entry<String, Double> entry : nextStates.entrySet()) {
	        entry.setValue(entry.getValue() / totalWeight);
	      }
	    }
	  }
	  
	  // Stores the current state, and retrieve a map of possible next states from the transducer.
	  // Compares all the possible next states, and return the top n most probable next states.
	  // Stores all the possible next states in descending order based on weights.
	  // If all the entries are the top n most probable next states, it randomly selects one from them.
	  public String predictNextWord(String currentState, int n) {
		    Map<String, Double> nextStates = transducer.get(currentState);
		    if (nextStates == null) {
		      return null;
		    }
		    PriorityQueue<Map.Entry<String, Double>> maxHeap = new PriorityQueue<>((a, b) -> Double.compare(b.getValue(), a.getValue()));
		    maxHeap.addAll(nextStates.entrySet());
		    List<Map.Entry<String, Double>> topNextStates = new ArrayList<>();
		    for (int i = 0; i < n; i++) {
		      Map.Entry<String, Double> entry = maxHeap.poll();
		      if (entry == null) {
		        break;
		      }
		      topNextStates.add(entry);
		    }
		    if (topNextStates.isEmpty()) {
		      return null;
		    }
		    return topNextStates.get(random.nextInt(topNextStates.size())).getKey();
		  }

	 // Part-of-speech tagging, it tags each word whether it is a verb, noun, adjective, etc.
	  public String[] tagPartsOfSpeech(String sentence) {
		    String[] words = sentence.split("\\s+");
		    String[] tags = new String[words.length];
		    for (int i = 0; i < words.length; i++) {
		        String word = words[i];
		        if (word.endsWith(".")) {
		            tags[i] = "END";
		        } else if (word.matches("\\d+(\\.\\d+)?")) {
		            tags[i] = "NUM";
		        } else if (word.matches("[A-Z][a-z]*")) {
		            tags[i] = "NOUN";
		        } else if (word.matches("[a-z]+(s|es)?")) {
		            tags[i] = "VERB";
		        } else if (word.matches("(is|are|was|were)")) {
		            tags[i] = "VERB";
		        } else if (word.matches("(a|an|the)")) {
		            tags[i] = "DET";
		        } else if (word.matches("(and|or|but)")) {
		            tags[i] = "CONJ";
		        } else if (word.matches("(in|on|at|to|of|from)")) {
		            tags[i] = "PREP";
		        } else if (word.matches("(who|what|where|when|why|how)")) {
		            tags[i] = "WH";
		        } else {
		            tags[i] = "UNK";
		        }
		    }
		    return tags;
		}
  
	  // Generate a next word or a series of predicted next words depending on the input (pre-typed strings) of the user.
	  // It contains a Language Tool library that checks and correct spelling and grammar errors of the predicted outputs.
	  public String predictNextWords(String currentState, int numWords, int n) throws IOException {
		    StringBuilder predictedWords = new StringBuilder();
		    for (int i = 0; i < numWords; i++) {
		      String nextState = predictNextWord(currentState, n);
		      if (nextState == null) {
		        JOptionPane.showMessageDialog(null, "No predictions found for the word: " + currentState,
		            "Error", JOptionPane.ERROR_MESSAGE);
		        break;
		      }
		      
		      predictedWords.append(nextState + " ");
		      currentState = nextState;
		    }
		    
		    String predictedText = predictedWords.toString();
		    
		    JLanguageTool languageTool = new JLanguageTool(new AmericanEnglish());
		    List<RuleMatch> matches = languageTool.check(predictedText);
		    if (matches.size() > 0) {
		        StringBuilder correctedText = new StringBuilder(predictedText);
		        for (int i = matches.size() - 1; i >= 0; i--) {
		            RuleMatch match = matches.get(i);
		            correctedText.replace(match.getFromPos(), match.getToPos(), match.getSuggestedReplacements().get(0));
		        }
		        predictedText = correctedText.toString();
		    }
		    
		    return predictedText;
		}
  
	  // Loads the training corpus as the dataset for the text-predictor.
  public void loadLibrary(File file) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] words = line.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
          addTransition(words[i], words[i + 1], 1.0);
        }
      }
    } catch (IOException e) {
      System.out.println("An error occurred while reading the file: " + e.getMessage());
    }
  }
  
  public void train(String library) {
    File file = new File(library);
    loadLibrary(file);
  }
  
  
  public String[] predict(String sentence, int numWords, int n) {
	    String[] words = sentence.split("\\s+");
	    String currentState = words[words.length - 1];
	    String[] tags = tagPartsOfSpeech(sentence);
	    // Pass the tags as context to the predictor
	    String predictedWords = predictNextWords(currentState, numWords, n, tags);
	    return predictedWords.split("\\s+");
	}

	public String predictNextWords(String currentState, int numWords, int n, String[] tags) {
	    StringBuilder predictedWords = new StringBuilder();
	    for (int i = 0; i < numWords; i++) {
	        String nextState = predictNextWord(currentState, n, tags);
	        if (nextState == null) {
	            JOptionPane.showMessageDialog(null, "No predictions found for the word: " + currentState,
	                    "Error", JOptionPane.ERROR_MESSAGE);
	            break;
	        }
	        predictedWords.append(nextState + " ");
	        currentState = nextState;
	    }
	    if (numWords == ' ') {
	        JOptionPane.showMessageDialog(null, "Please enter the number of words to be predicted. " + currentState, "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    return predictedWords.toString();
	}

	public String predictNextWord(String currentState, int n, String[] tags) {
	    Map<String, Double> nextStates = transducer.get(currentState);
	    if (nextStates == null) {
	        return null;
	    }
	    // Filter next states based on the tags
	    Map<String, Double> filteredStates =new HashMap<>();
	    for (Map.Entry<String, Double> entry : nextStates.entrySet()) {
	    	String nextState = entry.getKey();
	    	if (isValidNextState(nextState, tags)) {
	    	filteredStates.put(nextState, entry.getValue());
	    	}
	    	}
	    	if (filteredStates.isEmpty()) {
	    	return null;
	    	}
	    	
	    	// Get the n most probable next states
	    	List<Map.Entry<String, Double>> sortedStates = new ArrayList<>(filteredStates.entrySet());
	    	sortedStates.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
	    	List<String> topStates = new ArrayList<>();
	    	for (int i = 0; i < n && i < sortedStates.size(); i++) {
	    	topStates.add(sortedStates.get(i).getKey());
	    	}
	    	
	    	// Choose a next state at random from the top n states
	    	Random rand = new Random();
	    	int randomIndex = rand.nextInt(topStates.size());
	    	return topStates.get(randomIndex);
	    	}

	public boolean isValidNextState(String nextState, String[] tags) {
	    String[] nextStateWords = nextState.split("\\s+");
	    String[] nextStateTags = tagPartsOfSpeech(nextState);
	    	if (nextStateWords.length != nextStateTags.length) {
	    	return false;
	    	}
	    	
	    for (int i = 0; i < nextStateWords.length; i++) {
	    	if (i >= tags.length || !isValidTag(nextStateTags[i], tags[i])) {
	    	return false;
	    	}
	    	}
	    	return true;
	    	}

	    public boolean isValidTag(String nextTag, String currentTag) {
	    	// Check if nextTag is a valid tag to follow currentTag
	    	if (currentTag.equals("END")) {
	    	return false;
	    	}
	    	switch (currentTag) {
	    	case "NOUN":
	    	return nextTag.equals("VERB") || nextTag.equals("PREP") || nextTag.equals("END");
	    	case "VERB":
	    	return nextTag.equals("NOUN") || nextTag.equals("DET") || nextTag.equals("WH") || nextTag.equals("END");
	    	case "DET":
	    	return nextTag.equals("ADJ") || nextTag.equals("NOUN");
	    	case "ADJ":
	    	return nextTag.equals("NOUN");
	    	case "PREP":
	    	return nextTag.equals("NOUN") || nextTag.equals("WH");
	    	case "WH":
	    	return nextTag.equals("VERB") || nextTag.equals("NOUN") || nextTag.equals("ADJ") || nextTag.equals("PREP");
	    	default:
	    	return false;
	    	
	    	}
	    }

  }