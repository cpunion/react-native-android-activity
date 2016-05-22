import {AppRegistry, NativeModules, DeviceEventEmitter} from 'react-native'
const NativeActivityModule = NativeModules.Activity;

const DEFAULT_ACTIVITY_WARN_COUNT = 20

let requestCounter = 0
function generateRequestId() {
  const requestId = ++requestCounter
  if (requestCounter >= 21*10000*10000) {
    resultCounter = 0
  }
  return requestId
}

function generateComponentName() {
  counter++
  if (counter >= activityWarnCount) {
    console.warn(`Too many activities registered, there are ${counter} activities registered`)
  }
  return `REACT_ACTIVITY_${counter}`
}

let activityWarnCount = DEFAULT_ACTIVITY_WARN_COUNT
const activityRegistry = {}
let counter = 0

export default {
  setActivityWarnCount: (count) => {
    activityWarnCount = count
  },

  startActivity: (componentOrName, callback) => {
    const requestId = generateRequestId()

    let listenerCleaner

    const handleActivityResult = (result) => {
      if (!result.requestId || result.requestId === -1) {
        console.warn('Invalid requestId')
        return
      }

      if (result.requestId !== requestId) {
        return
      }

      listenerCleaner.remove()
      callback && callback()
    }

    let componentName

    if (typeof componentOrName === 'string') {
      componentName = componentOrName
    } else {
      const component = componentOrName

      componentName = activityRegistry[component]

      if (!componentName) {
        componentName = generateComponentName()
        activityRegistry[component] = componentName
        AppRegistry.registerComponent(componentName, () => component)
      }
    }

    if (callback) {
      listenerCleaner = DeviceEventEmitter.addListener("ACTIVITY_RESULT", handleActivityResult)
    }

    NativeActivityModule.startActivity(componentName, requestId)
  }
}
