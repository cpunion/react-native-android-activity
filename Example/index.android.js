/**
 * Example React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  TouchableOpacity,
  Text,
  View
} from 'react-native';
import Activity from 'react-native-android-activity';

class HelloPage extends Component {
  showActivity = () => {
    Activity.startActivity(HelloPage, () => {
      console.log('pop')
    })
  }

  render () {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.showActivity}>
          <View>
            <View>
              <Text>Hello,</Text>
            </View>
            <View>
              <Text>Activity!</Text>
            </View>
          </View>
        </TouchableOpacity>
      </View>
    )
  }
}

class Example extends Component {
  showActivity = () => {
    Activity.startActivity(HelloPage, () => {
      console.log('come back')
    })
  }

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.showActivity}>
          <View style={styles.container}>
            <Text style={styles.welcome}>
              Touch here to open activity
            </Text>
          </View>
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('Example', () => Example);
