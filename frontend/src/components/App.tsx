import '@radix-ui/themes/styles.css';
import { Theme, Flex } from '@radix-ui/themes';
import BetPanel from './BetPanel';
import Button from './button';       
import './BetPanel.css';

export default function App() {
  return (
    <Theme appearance="dark" accentColor="green" radius="large">          
      {/* Page wrapper */}
      <Flex
        direction="column"
        align="center"
        justify="center"
        gap="4"
        style={{ minHeight: '100svh', background: '#181717' }}
      >
        {/* Betting Panel */}
        <BetPanel yourBet={0} currentBet={0} />

        {/* Buttons under the panel */}
        <Flex gap="3" justify="center" align="center">
          <Button className="bet-button raise-button">Raise</Button>
          <Button className="bet-button fold-button" color="red">Fold</Button>
          <Button className="bet-button call-button" color="gray">Call</Button>
          <Button className="bet-button check-button"color="gray">Check</Button>
        </Flex>
      </Flex>
    </Theme>
  );
}
